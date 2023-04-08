package teleder.core.services.Message;

import teleder.core.dtos.PayloadAction;
import teleder.core.dtos.PayloadMessage;
import teleder.core.models.Message.Message;
import teleder.core.services.IMongoService;
import teleder.core.services.Message.dtos.CreateMessageDto;
import teleder.core.services.Message.dtos.MessageDto;
import teleder.core.services.Message.dtos.UpdateMessageDto;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface IMessageService extends IMongoService<MessageDto, CreateMessageDto, UpdateMessageDto> {
    public CompletableFuture<Message> sendPrivateMessage(String contactId, PayloadMessage message);

    public CompletableFuture<Message> sendGroupMessage(String groupId, PayloadMessage message);

    CompletableFuture<List<Message>> findMessagesWithPaginationAndSearch(long skip, int limit, String code, String content);

    CompletableFuture<Long> countMessagesByCode(String code);
    CompletableFuture<Message> markAsDelivered(String code);
    CompletableFuture<Message> markAsRead(String code);
    public CompletableFuture<Void> sendAction( PayloadAction input);
}
