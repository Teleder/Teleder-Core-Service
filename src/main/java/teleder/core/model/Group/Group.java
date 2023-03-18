package teleder.core.model.Group;

import lombok.Data;
import org.joda.time.DateTime;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import teleder.core.model.User.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Document(collection = "Group")
@Data
public class Group {
    @Id
    private String id;
    private String code_chat;
    private String code;
    @DBRef
    private User user_own_id;
    List<Member> member = new ArrayList<>();
    boolean isPublic;
    List<Block> block_list = new ArrayList<>();
    @CreatedBy
    private Date createdAt = new Date();
    @LastModifiedDate
    private Date updatedAt = new Date();
    boolean isDeleted = false;
}
