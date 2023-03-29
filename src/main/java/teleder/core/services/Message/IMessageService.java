package teleder.core.services.Message;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import teleder.core.models.Message.Message;
import teleder.core.services.IMongoService;

import java.util.List;

public interface IMessageService extends IMongoService<MessageDto> {
    public void sendPrivateMessage(@Payload Message message) ;
    public void sendGroupMessage(@Payload Message message);
}
