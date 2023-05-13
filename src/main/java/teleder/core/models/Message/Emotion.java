package teleder.core.models.Message;

import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Transient;
import teleder.core.models.User.User;

import java.util.Date;

@Data
public class Emotion {
    private String userId;
    private String emotion;
    @CreatedBy
    private Date createAt = new Date();
    @LastModifiedDate
    private Date updateAt = new Date();
    @Transient
    private User user;

    public Emotion(String userId, String emotion) {
        this.userId = userId;
        this.emotion = emotion;
    }

//    public String getString() {
//        if (this.userId == null)
//            return null;
//        return new String(userId.getId(), userId.getFirstName(), userId.getLastName(),
//                userId.getDisplayName(), userId.getBio(), userId.getAvatar(), userId.getQr(), userId.isActive(), userId.getLastActiveAt())
//                ;
//    }
}

