package teleder.core.services.Message;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import teleder.core.models.Message.Message;
import teleder.core.models.User.User;
import teleder.core.repositories.IGroupRepository;
import teleder.core.repositories.IMessageRepository;
import teleder.core.repositories.IUserRepository;

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
    public CompletableFuture<Message> getOne(String id) {
        return null;
    }

    @Override
    @Async
    public CompletableFuture<List<Message>> getAll() {
        return null;
    }

    @Override
    @Async
    public CompletableFuture<Message> update(String id, Message message) {
        return null;
    }

    @Override
    public void delete(String id) {

    }
@Override
    @MessageMapping("/privateMessage")
    public void sendPrivateMessage(@Payload Message message) {
        simpMessagingTemplate.convertAndSend("/topic/user." + message.getUser_send(), message);
    }
    @Override
    @MessageMapping("/groupMessage")
    public void sendGroupMessage(@Payload Message message) {
        simpMessagingTemplate.convertAndSend("/topic/group." + message.getGroup(), message);
    }
}
