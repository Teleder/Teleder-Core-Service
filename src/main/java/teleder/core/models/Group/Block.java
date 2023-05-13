package teleder.core.models.Group;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import teleder.core.models.User.User;

import java.util.Date;

@Data
public class Block {
    String reason;
    @Indexed(unique = true)
    private String userId;
    @Transient
    private User user;
    @CreatedDate
    private Date createAt = new Date();
    @LastModifiedDate
    private Date updateAt = new Date();
    public Block(String userId, String reason) {
        this.reason = reason;
        this.userId = userId;
    }
}
