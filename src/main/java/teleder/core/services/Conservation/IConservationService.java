package teleder.core.services.Conservation;

import org.springframework.scheduling.annotation.Async;
import teleder.core.services.Conservation.dtos.ConservationDto;
import teleder.core.services.Conservation.dtos.CreateConservationDto;
import teleder.core.services.Conservation.dtos.UpdateConservationDto;
import teleder.core.services.IMongoService;

import java.util.concurrent.CompletableFuture;

public interface IConservationService extends IMongoService<ConservationDto, CreateConservationDto, UpdateConservationDto> {
}
