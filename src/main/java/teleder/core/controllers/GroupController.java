package teleder.core.controllers;

import com.google.zxing.WriterException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import teleder.core.annotations.ApiPrefixController;
import teleder.core.annotations.Authenticate;
import teleder.core.dtos.PagedResultDto;
import teleder.core.dtos.Pagination;
import teleder.core.models.Group.Group;
import teleder.core.models.Group.Member;
import teleder.core.services.Group.IGroupService;
import teleder.core.services.Group.dtos.CreateGroupDto;
import teleder.core.services.Group.dtos.GroupDto;
import teleder.core.services.Group.dtos.RoleDto;
import teleder.core.services.User.dtos.UserBasicDto;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
@ApiPrefixController("groups")
public class GroupController {
    final
    IGroupService groupService;

    public GroupController(IGroupService groupService) {
        this.groupService = groupService;
    }

    @Authenticate
    @PostMapping("/create")
    public CompletableFuture<GroupDto> create(@RequestBody CreateGroupDto input) throws IOException, ExecutionException, InterruptedException, WriterException {
        String userId = ((UserDetails) (((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getAttribute("user"))).getUsername();
        return groupService.createGroup(userId, input);
    }

    @Authenticate
    @PatchMapping("/{groupId}/add-member")
    public CompletableFuture<Group> addMemberToGroup(@PathVariable String groupId, @RequestParam String memberId) {
        return groupService.addMemberToGroup(groupId, memberId);
    }

    @Authenticate
    @PatchMapping("/{groupId}/block-member")
    public CompletableFuture<Group> blockMemberFromGroup(@PathVariable String groupId, @RequestParam String memberId, @RequestParam String reason) {
        return groupService.blockMemberFromGroup(groupId, memberId, reason);
    }

    @Authenticate
    @PatchMapping("/{groupId}/remove-block-member")
    public CompletableFuture<Group> removeBlockMemberFromGroup(@PathVariable String groupId, @RequestParam String memberId) {
        return groupService.removeBlockMemberFromGroup(groupId, memberId);
    }

    @Authenticate
    @PatchMapping("/{groupId}/remove-member")
    public CompletableFuture<Group> removeMemberFromGroup(@PathVariable String groupId, @RequestParam String memberId) {
        return groupService.removeMemberFromGroup(groupId, memberId);
    }

    @Authenticate
    @PatchMapping("/{groupId}/decentralization")
    public CompletableFuture<Group> decentralization(@PathVariable String groupId, @RequestParam String memberId, @RequestParam String roleName) {
        return groupService.decentralization(groupId, memberId, roleName);
    }

    @Authenticate
    @GetMapping("/{groupId}/request-member-join")
    public CompletableFuture<List<Member>> getRequestMemberJoin(@PathVariable String groupId, @RequestParam String memberId) {
        return groupService.getRequestMemberJoin(groupId, memberId);
    }

    @Authenticate
    @PatchMapping("/{groupId}/response-member-join")
    public CompletableFuture<Void> responseMemberJoin(@PathVariable String groupId, @RequestParam String memberId, Boolean accept) {
        return groupService.responseMemberJoin(groupId, memberId, accept);
    }

    @Authenticate
    @GetMapping("/{groupId}/members-paginate")
    public PagedResultDto<Member> getMembersPaginate(@PathVariable String groupId, @RequestParam String search, @RequestParam long page, @RequestParam int size) {
        CompletableFuture<Integer> total = groupService.countMemberGroup(groupId, search);
        CompletableFuture<List<Member>> members = groupService.getMembersPaginate(groupId, search, page * size, size);
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(total, members);
        try {
            allFutures.get();
            return PagedResultDto.create(Pagination.create(total.get(), page * size, size), members.get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("Some thing went wrong!");
    }

    @Authenticate
    @GetMapping("/{groupId}/groups-paginate")
    public PagedResultDto<Group> getMyGroupsPaginate(@PathVariable String groupId, @RequestParam String search, @RequestParam long page, @RequestParam int size) {
        CompletableFuture<Long> total = groupService.countMyGroup();
        CompletableFuture<List<Group>> groups = groupService.getMyGroupsPaginate(groupId, search, page * size, size);
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(total, groups);
        try {
            allFutures.get();
            return PagedResultDto.create(Pagination.create(total.get(), page * size, size), groups.get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("Some thing went wrong!");
    }

    @Authenticate
    @PatchMapping("/{groupId}/leave-group")
    public CompletableFuture<Void> leaveGroup(@PathVariable String groupId) {
        return groupService.leaveGroup(groupId);
    }

    @Authenticate
    @PostMapping("/{groupId}/create-role")
    CompletableFuture<Group> createRoleForGroup(@PathVariable String groupId, @RequestBody RoleDto roleRequest) {
        return groupService.createRoleForGroup(groupId, roleRequest);
    }

    @Authenticate
    @DeleteMapping("/{groupId}/delete-role")
    public CompletableFuture<Void> deleteRoleForGroup(@PathVariable String groupId, @RequestParam String roleName) {
        return groupService.deleteRoleForGroup(groupId, roleName);
    }

    @Authenticate
    @GetMapping("/get-friend-add-group/{groupId}")
    public CompletableFuture<List<UserBasicDto>> getNonBlockedNonMemberFriends(@PathVariable String groupId) {
        String userId = ((UserDetails) (((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getAttribute("user"))).getUsername();
        return groupService.getNonBlockedNonMemberFriends(userId, groupId);
    }

    @Authenticate
    @GetMapping("/{groupId}")
    public CompletableFuture<GroupDto> getDetailGroup(@PathVariable String groupId) {
        String userId = ((UserDetails) (((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getAttribute("user"))).getUsername();
        return groupService.getDetailGroup(userId, groupId);
    }

}
