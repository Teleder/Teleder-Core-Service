package teleder.core.models.Conservation;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import teleder.core.annotations.JsonViews;
import teleder.core.dtos.UserConservationDto;
import teleder.core.models.BaseModel;
import teleder.core.models.Group.Group;
import teleder.core.models.Message.Message;
import teleder.core.models.User.User;
import teleder.core.utils.CONSTS;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Document(collection = "Conservation")
@Data
public class Conservation extends BaseModel {
    boolean status = true;
    @Id
    private String id;
    private String code = UUID.randomUUID().toString();
    private String type = CONSTS.MESSAGE_PRIVATE;
    private List<PinMessage> pinMessage = new ArrayList<>();
    @DBRef
    Message lastMessage;
    @DBRef
    private User user_1;
    @DBRef
    private User user_2;
    @DBRef
    private Group group;
    public Conservation(User user_1, User user_2, Group group) {
        this.user_2 = user_2;
        this.user_1 = user_1;
        this.group = group;
    }

    public User getUser_1() {
        return new User(this.user_1.getId(), this.user_1.getFirstName(), this.user_1.getLastName(),
                this.user_1.getDisplayName(), this.user_1.getBio(), this.user_1.getAvatar(), this.user_1.getQr(), this.user_1.isActive(), this.user_1.getLastActiveAt());
    }

    public User getUser_2() {
        return new User(this.user_2.getId(), this.user_2.getFirstName(), this.user_2.getLastName(),
                this.user_2.getDisplayName(), this.user_2.getBio(), this.user_2.getAvatar(), this.user_2.getQr(), this.user_2.isActive(), this.user_2.getLastActiveAt());
    }

    public Group getGroup() {
        return group;
    }

    public Conservation(Group group, String code) {
        this.group = group;
        this.code = code;
        this.type= CONSTS.MESSAGE_GROUP;
    }
    public Conservation() {
    }



    @Data
    public class PinMessage {
        @DBRef
        User pinBy;
        @DBRef
        private List<Message> pinMessage = new ArrayList<>();
    }

}

