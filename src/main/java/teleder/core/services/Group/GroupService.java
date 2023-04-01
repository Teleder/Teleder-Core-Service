package teleder.core.services.Group;

import org.bson.Document;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.ArrayOperators;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import teleder.core.dtos.ContactInfoDto;
import teleder.core.dtos.SocketPayload;
import teleder.core.exceptions.BadRequestException;
import teleder.core.exceptions.NotFoundException;
import teleder.core.exceptions.UnauthorizedException;
import teleder.core.models.Conservation.Conservation;
import teleder.core.models.Group.Block;
import teleder.core.models.Group.Group;
import teleder.core.models.Group.Member;
import teleder.core.models.Group.Role;
import teleder.core.models.Message.Message;
import teleder.core.models.Permission.Action;
import teleder.core.models.Permission.Permission;
import teleder.core.models.User.User;
import teleder.core.repositories.*;
import teleder.core.services.Group.dtos.CreateGroupDto;
import teleder.core.services.Group.dtos.GroupDto;
import teleder.core.services.Group.dtos.UpdateGroupDto;
import teleder.core.utils.CONSTS;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static teleder.core.models.Group.Member.Status.ACCEPT;
import static teleder.core.models.Group.Member.Status.WAITING;

@Service
public class GroupService implements IGroupService {
    final SimpMessagingTemplate simpMessagingTemplate;
    final IGroupRepository groupRepository;
    final IUserRepository userRepository;
    final IConservationRepository conservationRepository;
    final IPermissionRepository permissionRepository;
    final IMessageRepository messageRepository;
    final MongoTemplate mongoTemplate;

    public GroupService(SimpMessagingTemplate simpMessagingTemplate, IGroupRepository groupRepository, IUserRepository userRepository, IConservationRepository conservationRepository, IPermissionRepository permissionRepository, IMessageRepository messageRepository, MongoTemplate mongoTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
        this.conservationRepository = conservationRepository;
        this.permissionRepository = permissionRepository;
        this.messageRepository = messageRepository;
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    @Async
    public CompletableFuture<Group> createGroup(Group input) {
        String userId = ((User) (((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getAttribute("user"))).getId();
        User user = userRepository.findById(userId).orElse(null);
        if (user == null)
            throw new NotFoundException("Not found user");
        return CompletableFuture.completedFuture(groupRepository.save(input));
    }

    @Override
    @Async
    public CompletableFuture<Group> addMemberToGroup(String groupId, String memberId) {
        String userId = ((User) (((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getAttribute("user"))).getId();
        User user = null;
        User member = userRepository.findById(memberId).orElse(null);
        Group group = groupRepository.findById(groupId).orElse(null);
        if (member == null)
            throw new NotFoundException("Not found user");
        if (group == null)
            throw new NotFoundException("Not found group");
        if (userId != null) {
            user = userRepository.findById(userId).orElse(null);
            // check user exist
            Member memberFilter = group.getMembers().stream()
                    .filter(x -> x.getUserId().contains(userId))
                    .findFirst().orElse(null);
            if (memberFilter == null)
                throw new UnauthorizedException("You do not have permission to do that");
            // check block
            Block blockFilter = group.getBlock_list().stream()
                    .filter(x -> x.getUser_id().contains(memberId))
                    .findFirst().orElse(null);
            if (blockFilter != null)
                throw new BadRequestException("User has been block with reason: " + blockFilter.getReason() + " ,Please unlock if you are admin or member has permission to unlock before add new member");
            if (group.isPublic() || user.getId().contains(group.getUser_own().getId())) {
                group.getMembers().add(new Member(memberId, user, Member.Status.ACCEPT));
                member.getConservations().add(new Conservation(group, group.getCode()));
                // add conservation to user
                Message mess = new Message(user.getDisplayName() + " added " + member.getDisplayName() + " to group", group.getCode(), CONSTS.ADD_MEMBER_TO_GROUP);
                mess = messageRepository.save(mess);
                simpMessagingTemplate.convertAndSend("/messages/group." + groupId, SocketPayload.create(mess, CONSTS.ADD_MEMBER_TO_GROUP));
                userRepository.save(member);
            } else {
                group.getMembers().add(new Member(memberId, user, WAITING));
                simpMessagingTemplate.convertAndSend("/messages/group." + groupId, SocketPayload.create(new ContactInfoDto(member), CONSTS.REQUEST_MEMBER_TO_GROUP));
            }
        } else {
            // neu member tu request
            group.getMembers().add(new Member(memberId, user, WAITING));
            simpMessagingTemplate.convertAndSend("/messages/group." + groupId, SocketPayload.create(new ContactInfoDto(member), CONSTS.REQUEST_MEMBER_TO_GROUP));
        }

        return CompletableFuture.completedFuture(groupRepository.save(group));
    }

    @Override
    @Async
    public CompletableFuture<Group> blockMemberFromGroup(String groupId, String memberId, String reason) {
        String userId = ((User) (((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getAttribute("user"))).getId();
        User user = userRepository.findById(userId).orElse(null);
        User member = userRepository.findById(memberId).orElse(null);
        Group group = groupRepository.findById(groupId).orElse(null);
        if (user == null || member == null)
            throw new NotFoundException("Not found user");
        if (group == null)
            throw new NotFoundException("Not found group");
        // check block
        Block blockFilter = group.getBlock_list().stream()
                .filter(x -> x.getUser_id().contains(memberId))
                .findFirst().orElse(null);
        if (blockFilter != null)
            throw new BadRequestException("User has been block with reason: " + blockFilter.getReason());
        // check permission
        if (group.getUser_own().getId().contains(userId) ||
                getAction(group, userId).stream().filter(
                        x -> x.compareTo(Action.BLOCK) == 0 || x.compareTo(Action.ALL) == 0).findFirst().orElse(null) != null) {
            // them vao ds chan
            group.getBlock_list().add(new Block(userId, reason));
            // xoa khoi nhom
            leaveGroupFunc(group, member);
            userRepository.save(member);
            groupRepository.save(group);
            Message mess = new Message(member.getDisplayName() + " blocked by " + user.getDisplayName(), group.getCode(), CONSTS.BLOCK_MEMBER_GROUP);
            mess = messageRepository.save(mess);
            simpMessagingTemplate.convertAndSend("/messages/group." + groupId, SocketPayload.create(mess, CONSTS.BLOCK_MEMBER_GROUP));

            return null;
        } else {
            throw new UnauthorizedException("You do not have permission to do that");
        }
    }

    @Override
    @Async
    public CompletableFuture<Group> removeBlockMemberFromGroup(String groupId, String memberId) {
        String userId = ((User) (((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getAttribute("user"))).getId();
        User user = userRepository.findById(userId).orElse(null);
        User member = userRepository.findById(memberId).orElse(null);
        Group group = groupRepository.findById(groupId).orElse(null);
        if (user == null || member == null)
            throw new NotFoundException("Not found user");
        if (group == null)
            throw new NotFoundException("Not found group");
        // check block
        Block blockFilter = group.getBlock_list().stream()
                .filter(x -> x.getUser_id().contains(memberId))
                .findFirst().orElse(null);
        if (blockFilter != null)
            throw new BadRequestException("User has been block with reason: " + blockFilter.getReason());
        // check permission
        if (group.getUser_own().getId().contains(userId) ||
                getAction(group, userId).stream().filter(
                        x -> x.compareTo(Action.BLOCK) == 0 || x.compareTo(Action.ALL) == 0).findFirst().orElse(null) != null) {
            // them vao ds chan
            blockFilter = null;
            for (Block block : group.getBlock_list()) {
                if (block.getUser_id().contains(userId)) {
                    blockFilter = block;
                }
            }
            group.getBlock_list().remove(blockFilter);
            groupRepository.save(group);
            Message mess = new Message(user.getDisplayName() + "remove block for " + member.getDisplayName(), group.getCode(), CONSTS.REMOVE_BLOCK_MEMBER_GROUP);
            mess = messageRepository.save(mess);
            simpMessagingTemplate.convertAndSend("/messages/group." + groupId, SocketPayload.create(mess, CONSTS.REMOVE_BLOCK_MEMBER_GROUP));
            return null;
        } else {
            throw new UnauthorizedException("You do not have permission to do that");
        }
    }

    @Override
    @Async
    public CompletableFuture<Group> removeMemberFromGroup(String groupId, String memberId) {
        String userId = ((User) (((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getAttribute("user"))).getId();
        User user = userRepository.findById(userId).orElse(null);
        User member = userRepository.findById(memberId).orElse(null);
        Group group = groupRepository.findById(groupId).orElse(null);
        if (user == null || member == null)
            throw new NotFoundException("Not found user");
        if (group == null)
            throw new NotFoundException("Not found group");
        // check block
        Block blockFilter = group.getBlock_list().stream()
                .filter(x -> x.getUser_id().contains(memberId))
                .findFirst().orElse(null);
        if (blockFilter != null)
            throw new BadRequestException("User has been block with reason: " + blockFilter.getReason());
        // check permission
        if (group.getUser_own().getId().contains(userId) ||
                getAction(group, userId).stream().filter(
                        x -> x.compareTo(Action.DELETE) == 0 || x.compareTo(Action.ALL) == 0).findFirst().orElse(null) != null) {
            // xoa khoi nhom
            Member mem1 = null;
            for (Member mem : group.getMembers()) {
                if (mem.getUserId().contains(memberId)) {
                    mem1 = mem;
                    break;
                }
            }
            group.getMembers().remove(mem1);
            // xoa khoi conservation
            Conservation conservation = null;
            for (Conservation cons : member.getConservations()) {
                if (cons.getCode().contains(group.getCode())) {
                    conservation = cons;
                    break;
                }
            }
            user.getConservations().remove(conservation);
            userRepository.save(member);
            groupRepository.save(group);
            Message mess = new Message(member.getDisplayName() + " has been remove by " + user.getDisplayName(), group.getCode(), CONSTS.REMOVE_MEMBER);
            mess = messageRepository.save(mess);
            simpMessagingTemplate.convertAndSend("/messages/group." + groupId, SocketPayload.create(mess, CONSTS.REMOVE_MEMBER));
            return null;
        } else {
            throw new UnauthorizedException("You do not have permission to do that");
        }
    }

    @Override
    @Async
    public CompletableFuture<Group> decentralization(String groupId, String memberId, String roleName) {
        String userId = ((User) (((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getAttribute("user"))).getId();
        User user = userRepository.findById(userId).orElse(null);
        User member = userRepository.findById(memberId).orElse(null);
        Group group = groupRepository.findById(groupId).orElse(null);
        if (user == null || member == null)
            throw new NotFoundException("Not found user");
        if (group == null)
            throw new NotFoundException("Not found group");
        // check permission
        if (group.getUser_own().getId().contains(userId)) {
            // get role user
            // neu role la member thi la go role
            if (roleName.equals("member")){
                for (Member mem : group.getMembers()) {
                    if (mem.getUserId().contains(memberId)) {
                        mem.setRole(null);
                        break;
                    }
                }
                Message mess = new Message(member.getDisplayName() + " has removed role", group.getCode(), CONSTS.DECENTRALIZATION);
                mess = messageRepository.save(mess);
                simpMessagingTemplate.convertAndSend("/messages/group." + groupId, SocketPayload.create(mess, CONSTS.DECENTRALIZATION));
            }
            else{
                // cap role moi
                Role roleFilter = null;
                for (Role role : group.getRoles()) {
                    if (role.getName().contains(roleName)) {
                        roleFilter = role;
                        break;
                    }
                }
                // set lai role user
                for (Member mem : group.getMembers()) {
                    if (mem.getUserId().contains(memberId)) {
                        mem.setRole(roleFilter);
                        break;
                    }
                }
                Message mess = new Message(member.getDisplayName() + " is " + roleName, group.getCode(), CONSTS.DECENTRALIZATION);
                mess = messageRepository.save(mess);
                simpMessagingTemplate.convertAndSend("/messages/group." + groupId, SocketPayload.create(mess, CONSTS.DECENTRALIZATION));
            }

            groupRepository.save(group);

            return null;
        } else {
            throw new UnauthorizedException("You do not have permission to do that");
        }
    }

    @Override
    @Async
    public CompletableFuture<List<Member>> getRequestMemberJoin(String groupId, String memberId) {
        Group group = groupRepository.findById(groupId).orElse(null);
        if (group == null) {
            throw new NotFoundException("Not found group");
        }
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("_id").is(groupId)),
                Aggregation.match(Criteria.where("members.status").is(WAITING)),
                Aggregation.unwind("members"),
                Aggregation.sort(Sort.Direction.ASC, "members.createAt"),
                Aggregation.project()
                        .and("_id").as("id")
                        .and("list_contact.user").as("user")
                        .and("list_contact.status").as("status")
                        .and("list_contact.role").as("role")

        );
        List<Member> contacts = mongoTemplate.aggregate(aggregation, "Group", Member.class).getMappedResults();
        return CompletableFuture.completedFuture(contacts);
    }

    @Override
    @Async
    public CompletableFuture<Void> responseMemberJoin(String groupId, String memberId, Boolean accept) {
        if (accept) {
            Group group = groupRepository.findById(groupId).orElse(null);
            if (group == null) {
                throw new NotFoundException("Not found group");
            }
            User user = userRepository.findById(memberId).orElse(null);
            if (user == null)
                throw new NotFoundException("Not found user");
            // cho pheps vaof nhoms
            for (Member mem : group.getMembers()) {
                if (mem.getUserId().contains(memberId)) {
                    mem.setStatus(ACCEPT);
                    break;
                }
            }
            // them vao doan hoi thoai
            Conservation conservation = conservationRepository.findByCode(group.getCode());
            if (conservation == null)
                throw new NotFoundException("Not found conservation");
            user.getConservations().add(conservation);
            userRepository.save(user);
            groupRepository.save(group);
            Message mess = new Message(user.getDisplayName() + "has join group", group.getCode(), CONSTS.ACCEPT_MEMBER_JOIN);
            mess = messageRepository.save(mess);
            simpMessagingTemplate.convertAndSend("/messages/group." + groupId, SocketPayload.create(mess, CONSTS.ACCEPT_MEMBER_JOIN));
            return null;
        } else {
            Group group = groupRepository.findById(groupId).orElse(null);
            if (group == null) {
                throw new NotFoundException("Not found group");
            }
            Member memberFilter = null;
            for (Member mem : group.getMembers()) {
                if (mem.getUserId().contains(memberId)) {
                    memberFilter = mem;
                    break;
                }
            }
            if (memberFilter != null) {
                group.getMembers().remove(memberFilter);
                groupRepository.save(group);
            }
            simpMessagingTemplate.convertAndSend("/messages/group." + groupId, SocketPayload.create(new ContactInfoDto( memberFilter.getUser()), CONSTS.DENY_MEMBER_JOIN));
            return null;
        }
    }

    @Override
    @Async
    public CompletableFuture<List<Member>> getMembersPaginate(String groupId, String search, long skip, int limit) {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("_id").is(groupId)),
                Aggregation.unwind("members"),
                Aggregation.lookup("user", "members.userId", "_id", "members.user"),
                Aggregation.addFields()
                        .addFieldWithValue("members.userId", ArrayOperators.arrayOf("members.user").elementAt(0)).build(),
                Aggregation.match(Criteria.where("members.user.displayName").regex(Pattern.compile(search, Pattern.CASE_INSENSITIVE))),
                Aggregation.project("members"),
                Aggregation.sort(Sort.Direction.ASC, "list_contact.user.displayName"),
                Aggregation.skip(skip),
                Aggregation.limit(limit)
        );

        AggregationResults<Document> results = mongoTemplate.aggregate(aggregation, Group.class, Document.class);
        List<Document> documents = results.getMappedResults();

        return CompletableFuture.completedFuture(documents.stream()
                .map(doc -> {
                    Document memberDoc = doc.get("members", Document.class);
                    User user = mongoTemplate.getConverter().read(User.class, memberDoc.get("user", Document.class));
                    User addedBy = mongoTemplate.getConverter().read(User.class, memberDoc.get("addedBy", Document.class));
                    Member.Status status = Member.Status.valueOf(memberDoc.getString("status"));
                    Date createAt = memberDoc.getDate("createAt");
                    Date updateAt = memberDoc.getDate("updateAt");

                    Member member = new Member(user, addedBy, status);
                    member.setCreateAt(createAt);
                    member.setUpdateAt(updateAt);
                    return member;
                })
                .collect(Collectors.toList()));
    }

    @Override
    @Async
    public CompletableFuture<Integer> countMemberGroup(String groupId, String search) {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("_id").is(groupId)),
                Aggregation.unwind("members"),
                Aggregation.lookup("user", "members.userId", "_id", "members.user"),
                Aggregation.addFields()
                        .addFieldWithValue("members.userId", ArrayOperators.arrayOf("members.user").elementAt(0)).build(),
                Aggregation.match(Criteria.where("members.user.displayName").regex(Pattern.compile(search, Pattern.CASE_INSENSITIVE))),
                Aggregation.project("members"),
                Aggregation.sort(Sort.Direction.ASC, "list_contact.user.displayName"),
                Aggregation.group().count().as("count")
        );
        return CompletableFuture.completedFuture(mongoTemplate.aggregate(aggregation, Group.class, Document.class).getUniqueMappedResult().getInteger("count"));
    }

    @Override
    @Async
    public CompletableFuture<Long> countMyGroup() {
        String userId = ((User) (((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getAttribute("user"))).getId();
        User user = userRepository.findById(userId).orElse(null);
        if (user == null)
            throw new NotFoundException("Not found user");
        return CompletableFuture.completedFuture(conservationRepository.countUserMyGroups(user));
    }

    @Override
    @Async
    public CompletableFuture<List<Group>> getMyGroupsPaginate(String groupId, String search, long skip, int limit) {
        String userId = ((User) (((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getAttribute("user"))).getId();
        User user = userRepository.findById(userId).orElse(null);
        if (user == null)
            throw new NotFoundException("Not found user");
        return CompletableFuture.completedFuture(conservationRepository.getMyGroups(user, search, skip, limit));
    }

    @Override
    @Async
    public CompletableFuture<Void> leaveGroup(String groupId) {
        String userId = ((User) (((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getAttribute("user"))).getId();
        User user = userRepository.findById(userId).orElse(null);
        Group group = groupRepository.findById(groupId).orElse(null);
        if (user == null)
            throw new NotFoundException("Not found user");
        if (group == null)
            throw new NotFoundException("Not found group");
        Member memberFilter = group.getMembers().stream()
                .filter(x -> x.getUserId().contains(userId))
                .findFirst().orElse(null);
        if (memberFilter != null) {
            // xoa khoi nhom
            leaveGroupFunc(group, user);
            userRepository.save(user);
            groupRepository.save(group);
            Message mess = new Message(user.getDisplayName() + " has leave group", group.getCode(), CONSTS.LEAVE_GROUP);
            mess = messageRepository.save(mess);
            simpMessagingTemplate.convertAndSend("/messages/group." + groupId, SocketPayload.create(mess, CONSTS.LEAVE_GROUP));
            return null;
        } else {
            throw new UnauthorizedException("You do not have permission to do that");
        }
    }

    @Override
    @Async
    public CompletableFuture<Group> createRoleForGroup(String groupId, String roleName, List<Action> permissions) {
        String userId = ((User) (((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getAttribute("user"))).getId();
        User user = userRepository.findById(userId).orElse(null);
        Group group = groupRepository.findById(groupId).orElse(null);
        if (user == null)
            throw new NotFoundException("Not found user");
        if (group == null)
            throw new NotFoundException("Not found group");
        // check permission
        if (group.getUser_own().getId().contains(userId)) {
            List<Permission> pers = new ArrayList<>();
            permissions.stream().forEach(x -> {
                pers.add(permissionRepository.findByAction(x));
            });
            Role newRole = new Role(roleName, pers);
            group.getRoles().add(newRole);
            groupRepository.save(group);
            Message mess = new Message(user.getDisplayName() + " has added new role: " + roleName, group.getCode(), CONSTS.CREATE_ROLE);
            mess = messageRepository.save(mess);
            simpMessagingTemplate.convertAndSend("/messages/group." + groupId, SocketPayload.create(mess, CONSTS.CREATE_ROLE));
            return null;
        } else {
            throw new UnauthorizedException("You do not have permission to do that");
        }
    }

    @Override
    @Async
    public CompletableFuture<Void> deleteRoleForGroup(String groupId, String roleName) {
        String userId = ((User) (((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getAttribute("user"))).getId();
        User user = userRepository.findById(userId).orElse(null);
        Group group = groupRepository.findById(groupId).orElse(null);
        if (user == null)
            throw new NotFoundException("Not found user");
        if (group == null)
            throw new NotFoundException("Not found group");
        // check permission
        if (group.getUser_own().getId().contains(userId)) {
            // get role user
            Role roleFilter = null;
            for (Role role : group.getRoles()) {
                if (role.getName().contains(roleName)) {
                    roleFilter = role;
                    break;
                }
            }
            group.getRoles().remove(roleFilter);
            groupRepository.save(group);
            Message mess = new Message(roleName + " has remove!"  , group.getCode(), CONSTS.DELETE_ROLE);
            mess = messageRepository.save(mess);
            simpMessagingTemplate.convertAndSend("/messages/group." + groupId, SocketPayload.create(mess, CONSTS.DELETE_ROLE));
            return null;
        } else {
            throw new UnauthorizedException("You do not have permission to do that");
        }
    }


    // Basic CRUD
    @Override
    @Async
    public CompletableFuture<GroupDto> create(CreateGroupDto input) {


        return null;
    }


    @Override
    @Async
    public CompletableFuture<GroupDto> getOne(String id) {
        return null;
    }

    @Override
    @Async
    public CompletableFuture<List<GroupDto>> getAll() {
        return null;
    }

    @Override
    @Async
    public CompletableFuture<GroupDto> update(String id, UpdateGroupDto input) {
        return null;
    }


    @Override
    @Async
    public CompletableFuture<Void> delete(String id) {
        return null;
    }

    private List<Action> getAction(Group group, String userId) {
        Member member = group.getMembers().stream().filter(x -> x.getUserId().contains(userId)).findFirst().orElse(null);
        if (member == null || member.getRole() == null)
            return null;
        return member.getRole().getPermissions().stream().map(x -> x.getAction()).toList();
    }

    private void leaveGroupFunc(Group group, User user) {
        Member mem1 = null;
        for (Member mem : group.getMembers()) {
            if (mem.getUserId().contains(user.getId())) {
                mem1 = mem;
                break;
            }
        }
        group.getMembers().remove(mem1);
        // xoa khoi conservation
        Conservation conservation = null;
        for (Conservation cons : user.getConservations()) {
            if (cons.getCode().contains(group.getCode())) {
                conservation = cons;
                break;
            }
        }
        user.getConservations().remove(conservation);
    }
}
