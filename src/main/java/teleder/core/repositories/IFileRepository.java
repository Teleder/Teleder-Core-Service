package teleder.core.repositories;

import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import teleder.core.models.File.File;
import teleder.core.models.Message.Message;

import java.util.List;
import java.util.Optional;

public interface IFileRepository extends MongoRepository<File, String> {
    @Aggregation(pipeline = {
            "{ $match: { code: ?2 } }",
            "{ $sort: { createAt: -1 } }",
            "{ $skip: ?0 }",
            "{ $limit: ?1 }"
    })
    List<File> findFileWithPaginationAndSearch(long skip, int limit, String code);

    @Aggregation(pipeline = {
            "{ $match: { code: ?0 } }",
            "{ $group: { _id: null, count: { $sum: 1 } } }",
            "{ $project: { _id: 0 } }"
    })
    Optional<Long> countFileByCode(String code);
}
