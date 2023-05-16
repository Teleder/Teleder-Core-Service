package teleder.core.services.Conservation;

import org.springframework.scheduling.annotation.Async;
import teleder.core.dtos.PagedResultDto;
import teleder.core.models.Conservation.Conservation;
import teleder.core.services.Conservation.dtos.ConservationDto;
import teleder.core.services.Conservation.dtos.CreateConservationDto;
import teleder.core.services.Conservation.dtos.UpdateConservationDto;
import teleder.core.services.IMongoService;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface IConservationService extends IMongoService<ConservationDto, CreateConservationDto, UpdateConservationDto> {

    public CompletableFuture<PagedResultDto<Conservation>> getMyConversations(String userId, long skip, int limit);
    public CompletableFuture<List<String>> getAllIdConservationGroup(String userId);
}
