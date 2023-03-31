package teleder.core.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import teleder.core.models.Permission.Action;
import teleder.core.models.Permission.Permission;

public interface IPermissionRepository extends MongoRepository<Permission, String> {

    public Permission findByAction(Action action);
}
