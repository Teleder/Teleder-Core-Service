package teleder.core.models.Group;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.DBRef;
import teleder.core.models.Message.Message;
import teleder.core.models.User.User;

import java.util.ArrayList;
import java.util.List;

@Data
public class PinMessage {
    @DBRef
    User pinBy;
    @DBRef
    private List<Message> pinMessage = new ArrayList<>();
}