package teleder.core.models.Message;

import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.DBRef;
import teleder.core.models.User.User;

import java.util.Date;
@Data
public class Emotion {
    @DBRef
    private User user;
    private String emotion;
    @CreatedBy
    private Date createAt = new Date();
    @LastModifiedDate
    private Date updateAt = new Date();

    public Emotion(User user, String emotion){
        this.user = user;
        this.emotion = emotion;
    }

    public User getUser() {
        if (this.user == null)
            return null;
        return new User(user.getId(), user.getFirstName(), user.getLastName(),
                user.getDisplayName(), user.getBio(), user.getAvatar(), user.getQr(), user.isActive(), user.getLastActiveAt())
                ;
    }
}

