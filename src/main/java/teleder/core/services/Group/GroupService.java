package teleder.core.services.Group;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import teleder.core.models.Group.Group;
import teleder.core.models.User.User;
import teleder.core.repositories.IGroupRepository;
import teleder.core.repositories.IGroupRepository;
import teleder.core.repositories.IUserRepository;

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
    public CompletableFuture<Group> getOne(String id) {
        return null;
    }

    @Override
    @Async
    public CompletableFuture<List<Group>> getAll() {
        return null;
    }

    @Override
    @Async
    public CompletableFuture<Group> update(String id, Group Group) {
        return null;
    }

    @Override
    public void delete(String id) {

    }

}
