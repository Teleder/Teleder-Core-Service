package teleder.core.model.Message;

import lombok.Data;
import org.joda.time.DateTime;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import teleder.core.model.Group.Group;
import teleder.core.model.User.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Document(collection = "Message")
@Data
public class Message {
    @Id
    private String id;
    private String content;
    private String code;
    private TypeMessage type = TypeMessage.MESSAGE;
    @DBRef
    private User user_send_id;
    @DBRef
    private User user_receive_id;
    @DBRef
    private Group group_id;
    private List<Emotion> list_emotion = new ArrayList<>();
    boolean isDeleted = false;
    @CreatedBy
    private Date createdAt = new Date();
    @LastModifiedDate
    private Date updatedAt = new Date();
}
