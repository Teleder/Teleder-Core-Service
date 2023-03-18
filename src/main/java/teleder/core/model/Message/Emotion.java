package teleder.core.model.Message;

import lombok.Data;
import org.joda.time.DateTime;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.DBRef;
import teleder.core.model.User.User;

import java.util.Date;

@Data
public class Emotion {
    @DBRef
    private User user_id;
    private String emotion;
    @CreatedBy
    private Date createdAt = new Date();
    @LastModifiedDate
    private Date updatedAt = new Date();
}
