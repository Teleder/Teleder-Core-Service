package teleder.core.models.File;

import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import teleder.core.models.User.User;

import java.util.Date;

@Document(collection = "Group")
@Data
public class File {
    @Id
    private String id;
    String name;
    String file_type;
    double file_size;
    boolean isDeleted = false;
    @DBRef
    private User user_own;
    @CreatedBy
    private Date createAt = new Date();
    @LastModifiedDate
    private Date updateAt = new Date();
}
