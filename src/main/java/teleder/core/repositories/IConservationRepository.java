package teleder.core.repositories;

import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import teleder.core.models.Conservation.Conservation;
import teleder.core.models.Group.Group;
import teleder.core.models.User.User;

import java.util.List;
import java.util.Optional;

public interface IConservationRepository extends MongoRepository<Conservation, String> {

    @Query("{'code': ?0}")
    Conservation findByCode(String code);

    @Aggregation(pipeline = {
            "{$match: {user_1: ?0, group: {$ne: null}, 'name': { $regex: ?1, $options: 'i' }}}",
            "{$lookup: {from: 'group', localField: 'group', foreignField: '_id', as: 'group'}}",
            "{$unwind: '$group'}",
            "{$replaceRoot: {newRoot: '$group'}}",
            "{$skip: ?2}",
            "{$limit: ?3}"
    })
    List<Group> getMyGroups(User user, String search, long skip, int limit);

    @Query("{'user_1': ?0, 'group': {$ne: null}}")
    long countUserMyGroups(User user);

    @Query("{'groupId': ?0}")
    Optional<Conservation> findByGroupId(String groupId);

    @Query("{$or: [{$and: [{userId_1: ?0}, {userId_2: ?1}]}, {$and: [{userId_1: ?1}, {userId_2: ?0}]}]}")
    List<Conservation> getConservation(String userId, String contactId);

    @Query("{'id': { $in: ?0 }}")
    List<Conservation> findByIds(List<String> ids);

    @Query("{'id': { $in: ?0 }, 'groupId': {$ne: null}}")
    List<Conservation> getConservationGroup(List<String> ids);
}
