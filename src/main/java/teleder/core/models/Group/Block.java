package teleder.core.models.Group;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;

import java.util.Date;

@Data
public class Block {
    String reason;
    @Indexed(unique = true)
    private String user_id;
    @CreatedDate
    private Date createAt = new Date();
    @LastModifiedDate
    private Date updateAt = new Date();

    public Block(String user_id, String reason) {
        this.reason = reason;
        this.user_id = user_id;
    }
}
