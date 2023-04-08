package teleder.core.dtos;

import lombok.Data;

@Data
public class PayloadAction {
    String action;
    String receiverId;
    String receiverType;
    String typingMetadata;
    String code;
}
