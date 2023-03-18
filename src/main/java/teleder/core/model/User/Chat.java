package teleder.core.model.User;

import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.util.Date;

@Data
public class Chat {
    private String code;
    private Type type = Type.PERSONAL;
    @CreatedBy
    private Date createdAt = new Date();
    @LastModifiedDate
    private Date updatedAt = new Date();
}
