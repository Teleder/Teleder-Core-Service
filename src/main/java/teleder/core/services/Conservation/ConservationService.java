package teleder.core.services.Conservation;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import teleder.core.services.Conservation.dtos.ConservationDto;
import teleder.core.services.Conservation.dtos.CreateConservationDto;
import teleder.core.services.Conservation.dtos.UpdateConservationDto;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class ConservationService implements IConservationService {

    @Override
    public CompletableFuture<ConservationDto> create(CreateConservationDto input) {
        return null;
    }

    @Override
    @Async
    public CompletableFuture<ConservationDto> getOne(String id) {
        return null;
    }

    @Override
    @Async
    public CompletableFuture<List<ConservationDto>> getAll() {
        return null;
    }

    @Override
    @Async
    public CompletableFuture<ConservationDto> update(String id, UpdateConservationDto Conservation) {
        return null;
    }

    @Override
    @Async
    public CompletableFuture<Void> delete(String id) {
        return null;
    }

}
