package teleder.core.models.User;

import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Transient;

import java.util.Date;

@Data
public class Block {
    String reason;
    private String userId;
    @Transient
    private User user;
    @CreatedBy
    private Date createAt = new Date();
    @LastModifiedDate
    private Date updateAt = new Date();

    public Block(String userId, String reason) {
        this.userId = userId;
        this.reason = reason;
    }
}
