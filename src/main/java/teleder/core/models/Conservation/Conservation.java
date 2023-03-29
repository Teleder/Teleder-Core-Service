package teleder.core.models.Conservation;

import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import teleder.core.models.Message.Message;
import teleder.core.models.User.User;

import java.util.Date;
import java.util.List;
@Document(collection = "Conservation")
@Data
public class Conservation {
    private String code;
    private Type type = Type.PERSONAL;
    private List<PinMessage> pinMessage;
    @CreatedBy
    private Date createAt = new Date();
    @LastModifiedDate
    private Date updateAt = new Date();


    @Data
    public class PinMessage {
        @DBRef
        private List<Message> pinMessage;
        @DBRef
        User pinBy;
    }

    public enum Type {
        PERSONAL,
        GROUP
    }


}

