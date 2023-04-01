package teleder.core.models.Message;

import lombok.Data;
import lombok.NonNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import teleder.core.models.BaseModel;
import teleder.core.models.Group.Group;
import teleder.core.models.User.User;
import teleder.core.utils.CONSTS;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "Message")
@Data
public class Message extends BaseModel {
    @Id
    private String id;
    @NonNull
    private String content;
    @NonNull
    private String code;
    @NonNull
    private String TYPE = CONSTS.MESSAGE_PRIVATE;
    @DBRef
    private User user_send;
    @DBRef
    private User user_receive;
    @DBRef
    private Group group;
    private List<Emotion> list_emotion = new ArrayList<>();
    private List<HistoryChange> historyChanges = new ArrayList<>();

    public Message(String content, String code, String TYPE){
        this.code =code;
        this.content = content;
        this.TYPE = TYPE;
    }

}
