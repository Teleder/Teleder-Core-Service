package teleder.core.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import teleder.core.models.Group.Group;

public interface IGroupRepository extends MongoRepository<Group, String> {
}
