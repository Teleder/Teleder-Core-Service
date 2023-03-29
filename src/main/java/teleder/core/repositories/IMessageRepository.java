package teleder.core.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import teleder.core.models.Message.Message;

public interface IMessageRepository extends MongoRepository<Message, String> {
}
