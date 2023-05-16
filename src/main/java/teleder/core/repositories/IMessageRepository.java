package teleder.core.repositories;

import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import teleder.core.models.Message.Message;

import java.util.List;
import java.util.Optional;

public interface IMessageRepository extends MongoRepository<Message, String> {
    @Aggregation(pipeline = {
            "{  $match: { isDeleted: false, idParent: null,  code: ?2 ,  content: { $regex: ?3, $options: 'i'  }  } }",
            "{ $sort: { createAt: -1 } }",
            "{ $skip: ?0 }",
            "{ $limit: ?1 }"
    })
    List<Message> findMessagesWithPaginationAndSearch(long skip, int limit, String code, String content);

    @Aggregation(pipeline = {
            "{  $match: { isDeleted: false, idParent: null,$or: [ { code: ?0 }, { content: { $regex: ?1, $options: 'i' } } ] } }",
            "{ $group: { _id: null, count: { $sum: 1 } } }",
            "{ $project: { _id: 0 } }"
    })
    Optional<Long> countMessagesByCode(String code, String content);


    Optional<Message> findByCode(String code);

    @Aggregation(pipeline = {
            "{ $match: { isDeleted: false, code: ?0 } }",
            "{$sort: {'createAt': -1}}",
            "{$limit: 1}",
    })
    Optional<Message> findLastMessageByCode(String code);

}
