package teleder.core.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import teleder.core.annotations.ApiPrefixController;
import teleder.core.annotations.Authenticate;
import teleder.core.dtos.PagedResultDto;
import teleder.core.dtos.Pagination;
import teleder.core.models.Group.Group;
import teleder.core.models.Group.Member;
import teleder.core.models.Permission.Action;
import teleder.core.services.Group.IGroupService;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
@ApiPrefixController("groups")
public class GroupController {
    @Autowired
    IGroupService groupService;

//    @Authenticate
//    @PostMapping("/")
//    public CompletableFuture<Group> createGroup(@RequestBody Group input) {
//        return groupService.createGroup(input);
//    }

    @Authenticate
    @PatchMapping("/{id}/add-member")
    public CompletableFuture<Group> addMemberToGroup(@PathVariable String groupId, @RequestParam String memberId) {
        return groupService.addMemberToGroup(groupId, memberId);
    }

    @Authenticate
    @PatchMapping("/{id}/block-member")
    public CompletableFuture<Group> blockMemberFromGroup(@PathVariable String groupId, @RequestParam String memberId, @RequestParam String reason) {
        return groupService.blockMemberFromGroup(groupId, memberId, reason);
    }

    @Authenticate
    @PatchMapping("/{id}/remove-block-member")
    public CompletableFuture<Group> removeBlockMemberFromGroup(@PathVariable String groupId, @RequestParam String memberId) {
        return groupService.removeBlockMemberFromGroup(groupId, memberId);
    }

    @Authenticate
    @PatchMapping("/{id}/remove-member")
    public CompletableFuture<Group> removeMemberFromGroup(@PathVariable String groupId, @RequestParam String memberId) {
        return groupService.removeMemberFromGroup(groupId, memberId);
    }

    @Authenticate
    @PatchMapping("/{id}/decentralization")
    public CompletableFuture<Group> decentralization(@PathVariable String groupId, @RequestParam String memberId, @RequestParam String roleName) {
        return groupService.decentralization(groupId, memberId, roleName);
    }

    @Authenticate
    @GetMapping("/{id}/request-member-join")
    public CompletableFuture<List<Member>> getRequestMemberJoin(@PathVariable String groupId, @RequestParam String memberId) {
        return groupService.getRequestMemberJoin(groupId, memberId);
    }

    @Authenticate
    @PatchMapping("/{id}/response-member-join")
    public CompletableFuture<Void> responseMemberJoin(@PathVariable String groupId, @RequestParam String memberId, Boolean accept) {
        return groupService.responseMemberJoin(groupId, memberId, accept);
    }

    @Authenticate
    @GetMapping("/{id}/members-paginate")
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
    @GetMapping("/{id}/groups-pagninate")
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
    @PatchMapping("/{id}/leave-group")
    public CompletableFuture<Void> leaveGroup(@PathVariable String groupId) {
        return groupService.leaveGroup(groupId);
    }

    @Authenticate
    @PostMapping("/{id}/create-role")
    CompletableFuture<Group> createRoleForGroup(@PathVariable String groupId, String roleName, List<Action> permissions) {
        return groupService.createRoleForGroup(groupId, roleName, permissions);
    }

    @Authenticate
    @DeleteMapping("/{id}/delete-role")
    public CompletableFuture<Void> deleteRoleForGroup(@PathVariable String groupId, @RequestParam String roleName) {
        return groupService.deleteRoleForGroup(groupId, roleName);
    }
}
