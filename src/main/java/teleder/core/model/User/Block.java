package teleder.core.model.User;

import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.util.Date;

@Data
public class Block {
    @DBRef
    private User userId;
    String reason;
    @CreatedBy
    private Date createdAt= new Date();
    @LastModifiedDate
    private Date updatedAt= new Date();
}
