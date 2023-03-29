package teleder.core.services.Group;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import teleder.core.repositories.IGroupRepository;
import teleder.core.repositories.IUserRepository;
import teleder.core.services.Group.dtos.CreateGroupDto;
import teleder.core.services.Group.dtos.GroupDto;
import teleder.core.services.Group.dtos.UpdateGroupDto;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class GroupService implements IGroupService {
    @Autowired
    SimpMessagingTemplate simpMessagingTemplate;
    @Autowired
    IGroupRepository GroupRepository;
    @Autowired
    IUserRepository userRepository;
    @Autowired
    IGroupRepository groupRepository;

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

}
