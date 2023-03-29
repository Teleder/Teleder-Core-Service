package teleder.core.models.Group;

import lombok.Data;
import org.joda.time.DateTime;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.DBRef;
import teleder.core.models.User.User;

@Data
public class BlockList {
    String reason;
    @DBRef
    private User user;
    @CreatedDate
    private DateTime createAt = new DateTime();
    @LastModifiedDate
    private DateTime updateAt = new DateTime();
}
