package teleder.core.models.User;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.DBRef;

@Data
public class Contact {
    @DBRef
    User user;
    Status status;

    public Contact(User user, Status status) {
        this.user = user;
        this.status = status;
    }


    public User getUser() {
        return new User(this.user.getId(), this.user.getFirstName(), this.user.getLastName(),
                this.user.getDisplayName(), this.user.getBio(), this.user.getAvatar(), this.user.getQr(), this.user.isActive());
    }

    public enum Status {
        ACCEPT,
        WAITING,
        REQUEST
    }
}
