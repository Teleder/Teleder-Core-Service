package teleder.core.dtos;

import lombok.Data;
import teleder.core.models.Message.Message;

@Data
public class PayloadAction {
    String action;
    String receiverId;
    String receiverType;
    String typingMetadata;
    String code;
    String msgId;
    String emoji;
    Message message;
    String messageText;
}
