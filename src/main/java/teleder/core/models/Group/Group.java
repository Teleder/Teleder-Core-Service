package teleder.core.models.Group;

import lombok.Data;
import lombok.NonNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import teleder.core.models.BaseModel;
import teleder.core.models.File.File;
import teleder.core.models.Message.Message;
import teleder.core.models.User.User;

import java.util.*;

@Document(collection = "Group")
@Data
public class Group extends BaseModel {
    @NonNull
    String name;
    @NonNull
    String bio;
    String QR;
    Set<Role> roles = new HashSet<>();
    Set<Member> members = new HashSet<>();
    boolean isPublic;
    Set<Block> block_list = new HashSet<>();
    @Id
    private String id;
    private String code = UUID.randomUUID().toString();
    @DBRef
    private Set<Message> pinMessage = new HashSet<>();
    @DBRef
    private User user_own;
    @DBRef
    private File avatarGroup;
}
