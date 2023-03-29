package teleder.core.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import teleder.core.models.Conservation.Conservation;

public interface IConservationRepository extends MongoRepository<Conservation, String> {

}
