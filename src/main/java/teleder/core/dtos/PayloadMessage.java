package teleder.core.dtos;

import lombok.Data;
import lombok.NonNull;
import org.springframework.data.mongodb.core.mapping.DBRef;
import teleder.core.models.User.User;

@Data
public class PayloadMessage {
    private String content;
    private String code;
    private String TYPE;
    private String parentMessageId;
    private String group;
}
