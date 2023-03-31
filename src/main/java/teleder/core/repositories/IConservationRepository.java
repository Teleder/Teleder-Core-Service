package teleder.core.repositories;

import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import teleder.core.models.Conservation.Conservation;
import teleder.core.models.Group.Group;
import teleder.core.models.User.User;

import java.util.List;

public interface IConservationRepository extends MongoRepository<Conservation, String> {

    @Query("{'code': ?0}")
    Conservation findByCode(String code);
//    @Aggregation(pipeline = {
//            "{ $match: {  user_1: ObjectId(?0), group: { $ne: null } } }",
//            "{ $sort: { createAt: -1 } }",
//            "{ $skip: ?1 }",
//            "{ $limit: ?2 }",
//    })
//    List<Conservation> getMyGroups(User user, long skip, int limit);

    @Aggregation(pipeline = {
            "{$match: {user_1: ?0, group: {$ne: null}, 'name': { $regex: ?1, $options: 'i' }}}",
            "{$lookup: {from: 'group', localField: 'group', foreignField: '_id', as: 'group'}}",
            "{$unwind: '$group'}",
            "{$replaceRoot: {newRoot: '$group'}}",
            "{$skip: ?2}",
            "{$limit: ?3}"
    })
    List<Group> getMyGroups(User user,String search, long skip, int limit);

    @Query("{'user_1': ?0, 'group': {$ne: null}}")
    long countByUser1AndGroup(User user);
}
