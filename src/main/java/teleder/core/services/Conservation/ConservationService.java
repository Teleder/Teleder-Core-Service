package teleder.core.services.Conservation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import teleder.core.models.Conservation.Conservation;
import teleder.core.models.User.User;
import teleder.core.repositories.IGroupRepository;
import teleder.core.repositories.IConservationRepository;
import teleder.core.repositories.IUserRepository;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class ConservationService implements IConservationService {

    @Override
    @Async
    public CompletableFuture<Conservation> getOne(String id) {
        return null;
    }

    @Override
    @Async
    public CompletableFuture<List<Conservation>> getAll() {
        return null;
    }

    @Override
    @Async
    public CompletableFuture<Conservation> update(String id, Conservation Conservation) {
        return null;
    }

    @Override
    public void delete(String id) {

    }

}
