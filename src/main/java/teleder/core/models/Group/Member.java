package teleder.core.models.Group;

import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.DBRef;
import teleder.core.models.User.User;

import java.util.Date;

@Data
public class Member {
    @DBRef
    private User user;
    Role role;
    @CreatedBy
    private Date createAt = new Date();
    @LastModifiedDate
    private Date updateAt = new Date();
}