package teleder.core.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import teleder.core.models.User.User;

public interface IUserRepository extends MongoRepository<User, String> {
    User findByEmail(String email);
    User findByPhoneAndEmail(String input);
}
