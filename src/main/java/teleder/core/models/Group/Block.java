package teleder.core.models.Group;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.DBRef;
import teleder.core.models.User.User;

import java.util.Date;

@Data
public class Block {
    @DBRef
    private User user_id;
    String reason;
    @CreatedDate
    private Date createAt= new Date();
    @LastModifiedDate
    private Date updateAt= new Date();
}
