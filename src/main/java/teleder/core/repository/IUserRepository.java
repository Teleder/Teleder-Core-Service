package teleder.core.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import teleder.core.model.User.User;

public interface IUserRepository extends MongoRepository<User, String> {

}
