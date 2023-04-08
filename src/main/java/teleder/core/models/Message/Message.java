package teleder.core.models.Message;

import lombok.Data;
import lombok.NonNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import teleder.core.models.BaseModel;
import teleder.core.models.Group.Group;
import teleder.core.models.User.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Document(collection = "Message")
@Data
public class Message extends BaseModel implements Comparable<Message> {
    @Id
    private String id;
    @NonNull
    private String content;
    @NonNull
    private String code;
    @NonNull
    private String TYPE;
    @DBRef
    private User user_send;
    @DBRef
    private User user_receive;
    @DBRef
    Message parentMessageId;
    @DBRef
    private Group group;
    Date readAt = null;
    Date deliveredAt =null;
    String receiptType;


    public Message() {
    }

    public User getUser_receive() {
        if (this.user_receive == null)
            return null;
        return new User(user_receive.getId(), user_receive.getFirstName(), user_receive.getLastName(),
                user_receive.getDisplayName(), user_receive.getBio(), user_receive.getAvatar(), user_receive.getQr(), user_receive.isActive(), user_receive.getLastActiveAt());
    }

    public User getUser_send() {
        if (this.user_send == null)
            return null;
        return new User(user_send.getId(), user_send.getFirstName(), user_send.getLastName(),
                user_send.getDisplayName(), user_send.getBio(), user_send.getAvatar(), user_send.getQr(), user_send.isActive(), user_send.getLastActiveAt())
                ;
    }

    private List<Emotion> list_emotion = new ArrayList<>();
    private List<HistoryChange> historyChanges = new ArrayList<>();

    public Message(String content, String code, String TYPE) {
        this.code = code;
        this.content = content;
        this.TYPE = TYPE;
    }

    public Message(String content, String code, String TYPE, User user_send, User user_receive, Message parentMessageId) {
        this.code = code;
        this.content = content;
        this.TYPE = TYPE;
        this.user_send = user_send;
        this.user_receive = user_receive;
        this.parentMessageId = parentMessageId;
    }

    public Message(String content, String code, String TYPE, User user_send, Group group, Message parentMessageId) {
        this.code = code;
        this.content = content;
        this.TYPE = TYPE;
        this.user_send = user_send;
        this.user_receive = user_receive;
        this.parentMessageId = parentMessageId;
    }

    @Override
    public int compareTo(Message o) {
       return this.getCreateAt().compareTo(o.getCreateAt());
    }
}
