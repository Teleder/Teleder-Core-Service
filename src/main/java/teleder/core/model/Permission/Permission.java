package teleder.core.model.Permission;

import lombok.Data;
import org.joda.time.DateTime;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "Permission")
@Data
public class Permission {
    @Id
    private String id;
    Action action;
    @CreatedBy
    private Date createdAt = new Date();
    @LastModifiedDate
    private Date updatedAt = new Date();
    boolean isDeleted = false;
}
