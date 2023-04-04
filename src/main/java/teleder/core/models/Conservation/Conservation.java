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
    private Type type = Type.PERSONAL;
    private List<PinMessage> pinMessage = new ArrayList<>();
    private UserConservationDto user_1;
    private UserConservationDto user_2;
    @DBRef
    private Group group;
    public Conservation(UserConservationDto user_1, UserConservationDto user_2, Group group) {
        this.user_2 = user_2;
        this.user_1 = user_1;
        this.group = group;
    }

    public Conservation(Group group, String code) {
        this.group = group;
        this.code = code;
    }
    public Conservation() {
    }

    public enum Type {
        PERSONAL,
        GROUP
    }

    @Data
    public class PinMessage {
        @DBRef
        User pinBy;
        @DBRef
        private List<Message> pinMessage = new ArrayList<>();
    }

}

