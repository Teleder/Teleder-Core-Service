package teleder.core.models.Message;

import lombok.Data;
import lombok.NonNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import teleder.core.models.BaseModel;
import teleder.core.models.File.File;
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
    private String userId_send;
    private String userId_receive;
    @Transient
    private User user_send;
    @Transient
    private User user_receive;
    @DBRef
    List<Message> replyMessages = new ArrayList<>();
    private String groupId;
    @Transient
    private Group group;
    @DBRef
    private File file;
    String idParent = null;
    Date readAt = null;
    Date deliveredAt = null;

    private List<Emotion> list_emotion = new ArrayList<>();
    private List<HistoryChange> historyChanges = new ArrayList<>();

    public Message() {
    }
//    public String getString_receive() {
//        if (this.userId_receive == null)
//            return null;
//        return new String(userId_receive.getId(), userId_receive.getFirstName(), userId_receive.getLastName(),
//                userId_receive.getDisplayName(), userId_receive.getBio(), userId_receive.getAvatar(), userId_receive.getQr(), userId_receive.isActive(), userId_receive.getLastActiveAt());
//    }
//
//    public String getString_send() {
//        if (this.userId_send == null)
//            return null;
//        return new String(userId_send.getId(), userId_send.getFirstName(), userId_send.getLastName(),
//                userId_send.getDisplayName(), userId_send.getBio(), userId_send.getAvatar(), userId_send.getQr(), userId_send.isActive(), userId_send.getLastActiveAt())
//                ;
//    }

    public Message(String content, String code, String TYPE) {
        this.code = code;
        this.content = content;
        this.TYPE = TYPE;
    }


    public Message(String content, String code, String TYPE, String userId_send, String groupId, List<Message> replyMessages, File file) {
        this.code = code;
        this.content = content;
        this.TYPE = TYPE;
        this.userId_send = userId_send;
        this.groupId = groupId;
        this.replyMessages = replyMessages;
        this.file = file;
    }

    @Override
    public int compareTo(Message o) {
        return this.getCreateAt().compareTo(o.getCreateAt());
    }
}
