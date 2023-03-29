package teleder.core.models.User;

import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.util.Date;

@Data
public class Block {
    String reason;
    @DBRef
    private User user;
    @CreatedBy
    private Date createAt = new Date();
    @LastModifiedDate
    private Date updateAt = new Date();

    public Block(User user, String reason) {
        this.user = user;
        this.reason = reason;
    }
}
