package teleder.core.repositories;

import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import teleder.core.models.User.User;

import java.util.List;
import java.util.Optional;

public interface IUserRepository extends MongoRepository<User, String> {
    @Query(value = "{ $or: [ { 'email': ?0 }, { 'phone': ?0 } ] }")
    List<User> findByPhoneAndEmail(String input);

    @Query("{ $or: [ { 'email': ?0 }, { 'phone': ?0 }, { 'bio': ?0 } ] }")
    List<User> findContact(String input);

    Optional<User> findById(String id);
}
