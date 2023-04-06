package teleder.core.repositories;

import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import teleder.core.models.Message.Message;
import teleder.core.models.User.User;

import java.util.List;
import java.util.Optional;

public interface IMessageRepository extends MongoRepository<Message, String> {
    @Aggregation(pipeline = {
            "{ $match: { $or: [ { code: ?2 }, { content: { $regex: ?3, $options: 'i' } } ] } }",
            "{ $sort: { createAt: -1 } }",
            "{ $skip: ?0 }",
            "{ $limit: ?1 }"
    })
    List<Message> findMessagesWithPaginationAndSearch(long skip, int limit, String code, String content);

    @Aggregation(pipeline = {
            "{ $match: { code: ?0 } }",
            "{ $group: { _id: null, count: { $sum: 1 } } }",
            "{ $project: { _id: 0 } }"
    })
    Optional<Long> countMessagesByCode(String code);


    Optional<Message> findByCode(String code);

}
