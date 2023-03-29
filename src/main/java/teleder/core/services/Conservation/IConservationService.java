package teleder.core.services.Conservation;

import teleder.core.services.Conservation.dtos.ConservationDto;
import teleder.core.services.Conservation.dtos.CreateConservationDto;
import teleder.core.services.Conservation.dtos.UpdateConservationDto;
import teleder.core.services.IMongoService;

public interface IConservationService extends IMongoService<ConservationDto, CreateConservationDto, UpdateConservationDto> {

}
