package teleder.core.services.Permission;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import teleder.core.services.Permission.dtos.CreatePermissionDto;
import teleder.core.services.Permission.dtos.PermissionDto;
import teleder.core.services.Permission.dtos.UpdatePermissionDto;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class PermissionService implements IPermissionService {

    @Override
    @Async
    public CompletableFuture<PermissionDto> create(CreatePermissionDto input) {
        return null;
    }

    @Override
    @Async
    public CompletableFuture<PermissionDto> getOne(String id) {
        return null;
    }

    @Override
    @Async
    public CompletableFuture<List<PermissionDto>> getAll() {
        return null;
    }

    @Override
    @Async
    public CompletableFuture<PermissionDto> update(String id, UpdatePermissionDto input) {
        return null;
    }


    @Override
    @Async
    public CompletableFuture<Void> delete(String id) {
        return null;
    }

}
