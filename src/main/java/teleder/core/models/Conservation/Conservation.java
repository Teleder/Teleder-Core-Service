package teleder.core.models.Conservation;

import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import teleder.core.models.Group.Group;
import teleder.core.models.Message.Message;
import teleder.core.models.User.User;

import java.util.Date;
import java.util.List;

@Document(collection = "Conservation")
@Data
public class Conservation {
    @Id
    private String id;
    private String code;
    private Type type = Type.PERSONAL;
    private List<PinMessage> pinMessage;
    @CreatedBy
    private Date createAt = new Date();
    @LastModifiedDate
    private Date updateAt = new Date();
    boolean isDeleted = false;
    boolean status = true;
    @DBRef
    private User user_1;
    @DBRef
    private User user_2;
    @DBRef
    private Group group;

    public enum Type {
        PERSONAL,
        GROUP
    }

    @Data
    public class PinMessage {
        @DBRef
        User pinBy;
        @DBRef
        private List<Message> pinMessage;
    }

    public Conservation(User user_1,User user_2,  Group group) {
        this.user_2 = user_2;
        this.user_1 = user_1;
        this.group = group;
    }

}

