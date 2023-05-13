package teleder.core.models.User;

import lombok.Data;
import org.springframework.data.annotation.Transient;

@Data
public class Contact {
    String userId;
    Status status;
    @Transient
    User user;

    public Contact(String userId, Status status) {
        this.userId = userId;
        this.status = status;
    }


//    public User getUser() {
//        return new User(this.user.getId(), this.user.getFirstName(), this.user.getLastName(),
//                this.user.getDisplayName(), this.user.getBio(), this.user.getAvatar(), this.user.getQr(), this.user.isActive(), this.user.getLastActiveAt());
//    }

    public enum Status {
        ACCEPT,
        WAITING,
        REQUEST
    }
}
