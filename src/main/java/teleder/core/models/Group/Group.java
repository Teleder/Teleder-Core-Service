package teleder.core.models.Group;

import lombok.Data;
import lombok.NonNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import teleder.core.models.BaseModel;
import teleder.core.models.Message.Message;
import teleder.core.models.User.User;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Document(collection = "Group")
@Data
public class Group extends BaseModel {
    @Id
    private String id;
    @NonNull
    String name;
    @NonNull
    String bio;
    String QR;
    List<Role> roles = new ArrayList<>();
    List<Member> members = new ArrayList<>();
    boolean isPublic;
    List<Block> block_list = new ArrayList<>();

    private String code = UUID.randomUUID().toString();
    @DBRef
    private List<Message> pinMessage = new ArrayList<>();
    @DBRef
    private User user_own;
    private String avatarGroup;

    public Group() {

    }
}
