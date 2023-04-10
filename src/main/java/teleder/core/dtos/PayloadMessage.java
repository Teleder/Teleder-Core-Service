package teleder.core.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import teleder.core.models.File.File;

@Data
public class PayloadMessage {
    private String content;
    private String code;
    private String type;
    private String parentMessageId;
    private String group;
    private File file = null;
}
