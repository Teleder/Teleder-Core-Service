package teleder.core.model.Group;

import lombok.Data;
import org.joda.time.DateTime;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.MongoId;
import teleder.core.model.Permission.Permission;

@Data
public class BlockList {
    @DBRef
    private Permission user_id;
    String reason;
    @CreatedDate
    private DateTime createdAt= new DateTime();
    @LastModifiedDate
    private DateTime updatedAt= new DateTime();
}
