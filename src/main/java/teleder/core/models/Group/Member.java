package teleder.core.models.Group;

import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import teleder.core.models.User.User;
import teleder.core.services.User.dtos.UserBasicDto;
import teleder.core.utils.CONSTS;

import java.util.ArrayList;
import java.util.Date;

@Data
public class Member {
    Role role;
    Status status;
    @Indexed(unique = true)
    private String userId;
    @Transient
    private UserBasicDto user;
    @CreatedBy
    private Date createAt = new Date();
    @LastModifiedDate
    private Date updateAt = new Date();
    private String addedByUserId;
    public Member(String userId, String addedByUserId, Status status) {
        this.userId = userId;
        this.addedByUserId = addedByUserId;
        this.status = status;
        this.role =  new Role("Member", new ArrayList<>());
    }
    public Member(String userId) {
        this.userId = userId;
    }

    public Member() {
    }
    public Member(String userId, Status accept, Role owner,String addedByUserId ) {
        this.userId = userId;
        this.status = accept;
        this.role = owner;
        this.addedByUserId = addedByUserId;
    }
    public Member(String userId, Status accept, String addedByUserId, UserBasicDto user ,Role role) {
        this.userId = userId;
        this.status = accept;
        this.addedByUserId = addedByUserId;
        this.user = user;
        this.role = role;
    }
    public enum Status {
        ACCEPT,
        WAITING,
        REQUEST
    }
}