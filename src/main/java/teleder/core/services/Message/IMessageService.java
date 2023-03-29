package teleder.core.services.Message;

import org.springframework.messaging.handler.annotation.Payload;
import teleder.core.models.Message.Message;
import teleder.core.services.IMongoService;
import teleder.core.services.Message.dtos.CreateMessageDto;
import teleder.core.services.Message.dtos.MessageDto;
import teleder.core.services.Message.dtos.UpdateMessageDto;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface IMessageService extends IMongoService<MessageDto, CreateMessageDto, UpdateMessageDto> {
    void sendPrivateMessage(@Payload Message message);

    void sendGroupMessage(@Payload Message message);
    CompletableFuture<List<Message>> findMessagesWithPaginationAndSearch(long skip, int limit, String code, String content);
    CompletableFuture<Long> countMessagesByCode(String code);
}
