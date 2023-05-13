package teleder.core.utils;

import org.springframework.data.mongodb.core.MongoTemplate;
import teleder.core.models.Conservation.Conservation;
import teleder.core.models.Group.Group;
import teleder.core.models.User.User;

public class PopulateDocument {
    public static void populateConservation(MongoTemplate mongoTemplate, Conservation conservation) {
        if (conservation.getUserId_1() != null) {
            User user_1 = mongoTemplate.findById(conservation.getUserId_1(), User.class);
            conservation.setUser_1(user_1);
        }

        if (conservation.getUserId_2() != null) {
            User user_2 = mongoTemplate.findById(conservation.getUserId_2(), User.class);
            conservation.setUser_2(user_2);
        }

        if (conservation.getGroupId() != null) {
            Group group = mongoTemplate.findById(conservation.getGroupId(), Group.class);
            conservation.setGroup(group);
        }
    }
}
