package teleder.core.utils;

import org.modelmapper.ModelMapper;
import org.springframework.data.mongodb.core.MongoTemplate;
import teleder.core.models.Conservation.Conservation;
import teleder.core.models.Group.Group;
import teleder.core.models.User.User;
import teleder.core.services.Group.dtos.GroupDto;
import teleder.core.services.User.dtos.UserBasicDto;

public class PopulateDocument {
    public static void populateConservation(MongoTemplate mongoTemplate, Conservation conservation, ModelMapper toDto) {
        if (conservation.getType().equals(CONSTS.MESSAGE_PRIVATE)) {
            if (conservation.getUserId_1() != null) {
                User user_1 = mongoTemplate.findById(conservation.getUserId_1(), User.class);
                conservation.setUser_1(toDto.map(user_1, UserBasicDto.class));
            }

            if (conservation.getUserId_2() != null) {
                User user_2 = mongoTemplate.findById(conservation.getUserId_2(), User.class);
                conservation.setUser_2(toDto.map(user_2, UserBasicDto.class));
            }
        } else if (conservation.getGroupId() != null) {
            Group group = mongoTemplate.findById(conservation.getGroupId(), Group.class);
            conservation.setGroup(toDto.map(group, GroupDto.class));
        }
    }
}
