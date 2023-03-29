package teleder.core.models.User;

import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.util.Date;

@Data
public class Block {
    @DBRef
    private User user;
    String reason;
    @CreatedBy
    private Date createAt= new Date();
    @LastModifiedDate
    private Date updateAt= new Date();
}
