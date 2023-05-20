package teleder.core.services.Group;

import com.google.zxing.WriterException;
import teleder.core.models.Group.Group;
import teleder.core.models.Group.Member;
import teleder.core.models.User.User;
import teleder.core.services.Group.dtos.CreateGroupDto;
import teleder.core.services.Group.dtos.GroupDto;
import teleder.core.services.Group.dtos.RoleDto;
import teleder.core.services.Group.dtos.UpdateGroupDto;
import teleder.core.services.IMongoService;
import teleder.core.services.User.dtos.UserBasicDto;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public interface IGroupService extends IMongoService<GroupDto, CreateGroupDto, UpdateGroupDto> {
    public CompletableFuture<GroupDto> createGroup(String userId,CreateGroupDto input) throws IOException, WriterException, ExecutionException, InterruptedException;
    public CompletableFuture<Group> addMemberToGroup(String groupId, String memberId);
    public CompletableFuture<Group> blockMemberFromGroup(String groupId, String memberId, String reason);
    public CompletableFuture<Group> removeBlockMemberFromGroup(String groupId, String memberId);
    public CompletableFuture<Group> removeMemberFromGroup(String groupId, String memberId);
    public CompletableFuture<Group> decentralization(String groupId, String memberId, String roleName);
    public CompletableFuture<List<Member>> getRequestMemberJoin(String groupId, String memberId);
    public CompletableFuture<Void> responseMemberJoin(String groupId, String memberId, Boolean accept);
    public CompletableFuture<List<Member>> getMembersPaginate(String groupId, String search, long skip, int limit);
    public CompletableFuture<List<Group>> getMyGroupsPaginate(String groupId, String search, long skip, int limit);
    public CompletableFuture<Void> leaveGroup(String groupId);
    CompletableFuture<Group> createRoleForGroup(String groupId, RoleDto roleRequest);
    public CompletableFuture<Void> deleteRoleForGroup(String groupId, String roleName);
    public CompletableFuture<Integer> countMemberGroup(String groupId, String search);
    public CompletableFuture<Void> delete(String userID, String id);
    public CompletableFuture<Long> countMyGroup();
    public CompletableFuture<List<UserBasicDto>> getNonBlockedNonMemberFriends(String userId, String groupId);
    public CompletableFuture<GroupDto> getDetailGroup(String userId, String groupId);
    public CompletableFuture<List<UserBasicDto>> getWaitingAccept(String groupId);
}
