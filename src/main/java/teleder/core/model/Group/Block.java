package teleder.core.model.Group;

import lombok.Data;
import org.joda.time.DateTime;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.DBRef;
import teleder.core.model.User.User;

import java.util.Date;

@Data
public class Block {
    @DBRef
    private User user_id;
    String reason;
    @CreatedDate
    private Date createdAt= new Date();
    @LastModifiedDate
    private Date updatedAt= new Date();
}
