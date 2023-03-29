package teleder.core.models.Group;

import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import teleder.core.models.Message.Message;
import teleder.core.models.User.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Document(collection = "Group")
@Data
public class Group {
    @Id
    private String id;
    private String code;
    @DBRef
    private List<Message>  pinMessage;
    @DBRef
    private User user_own;
    List<Member> member = new ArrayList<>();
    boolean isPublic;
    List<Block> block_list = new ArrayList<>();
    @CreatedBy
    private Date createAt = new Date();
    @LastModifiedDate
    private Date updateAt = new Date();
    boolean isDeleted = false;
}
