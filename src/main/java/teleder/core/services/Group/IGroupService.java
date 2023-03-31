package teleder.core.services.Group;

import teleder.core.models.Group.Group;
import teleder.core.models.Group.Member;
import teleder.core.models.Permission.Action;
import teleder.core.services.Group.dtos.CreateGroupDto;
import teleder.core.services.Group.dtos.GroupDto;
import teleder.core.services.Group.dtos.UpdateGroupDto;
import teleder.core.services.IMongoService;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface IGroupService extends IMongoService<GroupDto, CreateGroupDto, UpdateGroupDto> {
    public CompletableFuture<Group> createGroup(Group input);
    public CompletableFuture<Group> addMemberToGroup(String groupId, String memberId);
    public CompletableFuture<Group> blockMemberFromGroup(String groupId, String memberId, String reason);
    public CompletableFuture<Group> removeBlockMemberFromGroup(String groupId, String memberId);
    public CompletableFuture<Group> removeMemberFromGroup(String groupId, String memberId);
    public CompletableFuture<Group> decentralization(String groupId, String memberId, String roleName);
    public CompletableFuture<List<Member>> getRequestMemberJoin(String groupId, String memberId);
    public CompletableFuture<Void> responseMemberJoin(String groupId, String memberId, Boolean accept) ;
    public CompletableFuture<List<Member>> getMembersPaginate(String groupId, String search, long skip, int limit);
    public CompletableFuture<List<Group>> getMyGroupsPaginate(String groupId, String search, long skip, int limit);
    public CompletableFuture<Void> leaveGroup(String groupId);
    CompletableFuture<Group> createRoleForGroup(String groupId,  String roleName, List<Action> permissions);
    public CompletableFuture<Void> deleteRoleForGroup(String groupId, String roleName);
    public CompletableFuture<Integer> countMermberGroup(String groupId, String search);
    public CompletableFuture<Long> countMyGroup();
}
