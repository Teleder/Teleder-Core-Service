package teleder.core.models.Conservation;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import teleder.core.models.BaseModel;
import teleder.core.models.Group.PinMessage;
import teleder.core.models.Message.Message;
import teleder.core.services.Group.dtos.GroupDto;
import teleder.core.services.User.dtos.UserBasicDto;
import teleder.core.utils.CONSTS;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Document(collection = "Conservation")
@Data
public class Conservation extends BaseModel implements Comparable<Conservation> {
    boolean status = true;
    @Id
    private String id;
    private String code = UUID.randomUUID().toString();
    private String type = CONSTS.MESSAGE_PRIVATE;
    private List<PinMessage> pinMessage = new ArrayList<>();
    @DBRef
    Message lastMessage;
    private String userId_1;
    private String currentEmotion = "\uD83D\uDC4D";
    @Transient
    private UserBasicDto user_1;
    private String userId_2;
    @Transient
    private UserBasicDto user_2;
    private String groupId;
    @Transient
    private GroupDto group;

    public Conservation(String userId_1, String userId_2, String groupId) {
        this.userId_2 = userId_2;
        this.userId_1 = userId_1;
        this.groupId = groupId;
    }


    public Conservation(String groupId) {
        this.groupId = groupId;
        this.code = String.valueOf(UUID.randomUUID());
        this.type = CONSTS.MESSAGE_GROUP;
    }

    public Conservation() {
    }

    @Override
    public int compareTo(Conservation o) {
        return o.getUpdateAt().compareTo(this.getCreateAt());
    }
}

