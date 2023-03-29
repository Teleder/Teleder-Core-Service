package teleder.core.services.Permission;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import teleder.core.models.Permission.Permission;
import teleder.core.models.User.User;
import teleder.core.repositories.IGroupRepository;
import teleder.core.repositories.IUserRepository;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class PermissionService implements IPermissionService {

    @Override
    @Async
    public CompletableFuture<Permission>  getOne(String id) {
        return null;
    }

    @Override
    @Async
    public CompletableFuture<List<Permission>>  getAll() {
        return null;
    }

    @Override
    @Async
    public CompletableFuture<Permission > update(String id, Permission Permission) {
        return null;
    }

    @Override
    public void delete(String id) {

    }

}
