package teleder.core.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import teleder.core.models.Conservation.Conservation;
import teleder.core.models.User.User;

public interface IConservationRepository extends MongoRepository<Conservation, String> {

}
