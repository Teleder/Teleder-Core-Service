package teleder.core.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import teleder.core.models.Permission.Permission;

public interface IPermissionService extends MongoRepository<Permission, String> {
}
