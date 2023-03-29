package teleder.core.services.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import teleder.core.models.File.File;
import teleder.core.models.User.User;
import teleder.core.repositories.IGroupRepository;
import teleder.core.repositories.IUserRepository;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class FileService implements IFileService {
    @Override
    @Async
    public CompletableFuture<File> getOne(String id) {
        return null;
    }

    @Override
    @Async
    public CompletableFuture<List<File>> getAll() {
        return null;
    }

    @Override
    @Async
    public CompletableFuture<File> update(String id, File File) {
        return null;
    }

    @Override
    public void delete(String id) {

    }


}
