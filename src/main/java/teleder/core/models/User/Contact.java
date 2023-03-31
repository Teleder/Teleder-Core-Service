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

    public enum Status {
        ACCEPT,
        WAITING,
        REQUEST
    }
}
