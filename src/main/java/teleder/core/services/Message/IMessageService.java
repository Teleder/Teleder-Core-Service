package teleder.core.services.Message;

import teleder.core.dtos.PagedResultDto;
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

    public CompletableFuture<Long> countMessagesByCode(String code, String content);

    CompletableFuture<Message> markAsDelivered(String code);

    CompletableFuture<Message> markAsRead(String id);

    public CompletableFuture<Object> sendAction(PayloadAction input);

    public CompletableFuture<List<Message>> getReplyMessage(String id);
    public CompletableFuture<PagedResultDto<Message>> findMessagesByIdUser(long skip, int limit, String content, String contactId);
}
