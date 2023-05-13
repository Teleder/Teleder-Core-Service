package teleder.core.models.Group;

import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import teleder.core.models.User.User;

import java.util.Date;

@Data
public class Member {
    Role role;
    Status status;
    @Indexed(unique = true)
    private String userId;
    @Transient
    private User user;
    @CreatedBy
    private Date createAt = new Date();
    @LastModifiedDate
    private Date updateAt = new Date();
    private String addedByUserId;
    public Member(String userId, String addedByUserId, Status status) {
        this.userId = userId;
        this.addedByUserId = addedByUserId;
        this.status = status;
    }

    public enum Status {
        ACCEPT,
        WAITING,
        REQUEST
    }
}