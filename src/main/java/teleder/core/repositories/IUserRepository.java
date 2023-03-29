package teleder.core.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import teleder.core.models.User.User;

public interface IUserRepository extends MongoRepository<User, String> {
    @Query("{ $or: [ { 'email': ?0 }, { 'phone': ?0 } ] }")
    User findByPhoneAndEmail(String input);
    @Query("{ $or: [ { 'email': ?0 }, { 'phone': ?0 }, { 'bio': ?0 } ] }")
    User findContact(String input);


}
