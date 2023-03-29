package teleder.core.services.Message;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import teleder.core.exceptions.NotFoundException;
import teleder.core.models.Message.Message;
import teleder.core.models.User.User;
import teleder.core.repositories.IGroupRepository;
import teleder.core.repositories.IMessageRepository;
import teleder.core.repositories.IUserRepository;
import teleder.core.services.Message.dtos.CreateMessageDto;
import teleder.core.services.Message.dtos.MessageDto;
import teleder.core.services.Message.dtos.UpdateMessageDto;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class MessageService implements IMessageService {
    @Autowired
    SimpMessagingTemplate simpMessagingTemplate;
    @Autowired
    IMessageRepository messageRepository;
    @Autowired
    IUserRepository userRepository;
    @Autowired
    IGroupRepository groupRepository;

    @Override
    @Async
    public CompletableFuture<MessageDto> create(CreateMessageDto input) {
        return null;
    }

    @Override
    @Async
    public CompletableFuture<MessageDto> getOne(String id) {
        return null;
    }

    @Override
    @Async
    public CompletableFuture<List<MessageDto>> getAll() {
        return null;
    }

    @Override
    @Async
    public CompletableFuture<MessageDto> update(String id, UpdateMessageDto input) {
        return null;
    }


    @Override
    @Async
    public CompletableFuture<Void> delete(String id) {
        return null;
    }


    @Override
    @MessageMapping("/privateMessage")
    public void sendPrivateMessage(@Payload Message message) {
        String userId = ((User) (((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getAttribute("user"))).getId();
        if (!userRepository.findById(userId).get().getConservations().stream().anyMatch(elem -> elem.getCode().equals(message.getCode())))
            throw new NotFoundException("Not Found Conservation!");
        simpMessagingTemplate.convertAndSend("/topic/user." + userId, message);
    }

    @Override
    @MessageMapping("/groupMessage")
    public void sendGroupMessage(@Payload Message message) {
        String userId = ((User) (((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getAttribute("user"))).getId();
        if (!userRepository.findById(userId).get().getConservations().stream().anyMatch(elem -> elem.getCode().equals(message.getCode())))
            throw new NotFoundException("Not Found Conservation!");
        simpMessagingTemplate.convertAndSend("/topic/group." + message.getGroup(), message);
    }

    @Override
    public CompletableFuture<List<Message>> findMessagesWithPaginationAndSearch(long skip, int limit, String code, String content) {
        String userId = ((User) (((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getAttribute("user"))).getId();
        if (!userRepository.findById(userId).get().getConservations().stream().anyMatch(elem -> elem.getCode().equals(code)))
            throw new NotFoundException("Not Found Conservation!");
        List<Message> messages = messageRepository.findMessagesWithPaginationAndSearch(skip, limit, code, content);
        return CompletableFuture.completedFuture(messages);
    }

    @Override
    public CompletableFuture<Long> countMessagesByCode(String code) {
        return CompletableFuture.supplyAsync(() -> messageRepository.countMessagesByCode(code).orElse(0L));
    }
}
