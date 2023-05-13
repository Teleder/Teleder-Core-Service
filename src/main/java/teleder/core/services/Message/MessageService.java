package teleder.core.services.Message;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import teleder.core.dtos.*;
import teleder.core.exceptions.NotFoundException;
import teleder.core.models.Conservation.Conservation;
import teleder.core.models.Group.Group;
import teleder.core.models.Message.Emotion;
import teleder.core.models.Message.HistoryChange;
import teleder.core.models.Message.Message;
import teleder.core.models.User.User;
import teleder.core.repositories.IConservationRepository;
import teleder.core.repositories.IGroupRepository;
import teleder.core.repositories.IMessageRepository;
import teleder.core.repositories.IUserRepository;
import teleder.core.services.Message.dtos.CreateMessageDto;
import teleder.core.services.Message.dtos.MessageDto;
import teleder.core.services.Message.dtos.UpdateMessageDto;
import teleder.core.utils.CONSTS;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class MessageService implements IMessageService {
    private final MongoTemplate mongoTemplate;
    final
    SimpMessagingTemplate simpMessagingTemplate;
    final
    IMessageRepository messageRepository;
    final
    IUserRepository userRepository;
    final
    IConservationRepository conservationRepository;
    private final ModelMapper toDto;
    private final IGroupRepository iGroupRepository;

    public MessageService(SimpMessagingTemplate simpMessagingTemplate, IMessageRepository messageRepository, IUserRepository userRepository, IConservationRepository conservationRepository, ModelMapper toDto,
                          IGroupRepository iGroupRepository, MongoTemplate mongoTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.conservationRepository = conservationRepository;
        this.toDto = toDto;
        this.iGroupRepository = iGroupRepository;
        this.mongoTemplate = mongoTemplate;
    }

    @Async
    @Override
    public CompletableFuture<Message> sendPrivateMessage(String contactId, PayloadMessage messagePayload) {
        String userId = ((UserDetails) (((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getAttribute("user"))).getUsername();
        // check conservation da tao hay chua neu chua tao thi tao moi
        User user = userRepository.findById(userId).orElse(null);
        User contact = userRepository.findById(contactId).orElse(null);
        Message message = new Message(messagePayload.getContent(), messagePayload.getCode(), messagePayload.getType(), userId, contactId, null, messagePayload.getFile());
        if (user == null || contact == null)
            throw new NotFoundException("Not found user");
        message.setUserId_send(userId);
        message.setUserId_receive(contactId);
        message.setCode(messagePayload.getCode());
        if (messagePayload.getParentMessageId() == null) {
            message = messageRepository.save(message);
            Conservation conservation = user.getConservations().stream()
                    .filter(x -> x.getUserId_1().equals(contactId) || x.getUserId_2().equals(contactId))
                    .findFirst().orElse(null);
            if (conservation == null) {
                conservation = new Conservation(userId, message.getUserId_receive(), null);
                conservation = conservationRepository.save(conservation);
                user.getConservations().add(conservation);
                contact.getConservations().add(conservation);
                user.setConservations(user.getConservations());
                contact.setConservations(contact.getConservations());
                userRepository.save(user);
                userRepository.save(contact);
            }
            // add tin nhan vao db

            conservation = conservationRepository.findByCode(message.getCode());
            conservation.setLastMessage(message);
            conservationRepository.save(conservation);
        } else {
            Message parentMessage = messageRepository.findById(messagePayload.getParentMessageId()).orElse(null);
            if (parentMessage == null)
                throw new NotFoundException("Not found message reply");
            message.setIdParent(messagePayload.getParentMessageId());
            message = messageRepository.save(message);
            parentMessage.getReplyMessages().add(message);
            messageRepository.save(parentMessage);
        }

        simpMessagingTemplate.convertAndSend("/messages/user." + contactId, SocketPayload.create(message, CONSTS.MESSAGE_PRIVATE));
        return CompletableFuture.completedFuture(message);
    }

    @Async
    @Override
    public CompletableFuture<Object> sendAction(PayloadAction input) {
        String userId = ((UserDetails) (((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getAttribute("user"))).getUsername();
        User user = userRepository.findById(userId).orElse(null);
        if (user == null)
            throw new NotFoundException("Not found user");
        switch (input.getAction()) {
            case CONSTS.CHATTING:
            case CONSTS.STOP_CHATTING: {
                User contact = userRepository.findById(input.getReceiverId()).orElse(null);
                if (contact == null)
                    throw new NotFoundException("Not found user");
                if (input.getReceiverType() == CONSTS.MESSAGE_GROUP)
                    simpMessagingTemplate.convertAndSend("/messages/group." + input.getReceiverId(), SocketPayload.create(input, input.getAction()));
                else
                    simpMessagingTemplate.convertAndSend("/messages/user." + input.getReceiverId(), SocketPayload.create(input, input.getAction()));
                return CompletableFuture.completedFuture(null);
            }
            case CONSTS.EMOJI: {
                Message mess = messageRepository.findById(input.getMsgId()).orElse(null);
                if (mess == null)
                    throw new NotFoundException("Not found message");
                mess.getList_emotion().add(new Emotion(userId, input.getEmoji()));
                mess = messageRepository.save(mess);
                if (input.getReceiverType() == CONSTS.MESSAGE_GROUP)
                    simpMessagingTemplate.convertAndSend("/messages/group." + input.getReceiverId(), SocketPayload.create(input, input.getAction()));
                else
                    simpMessagingTemplate.convertAndSend("/messages/user." + input.getReceiverId(), SocketPayload.create(input, input.getAction()));
                return CompletableFuture.completedFuture(mess);
            }
            case CONSTS.NEW_REACTION: {
                if (input.getReceiverType() == CONSTS.MESSAGE_GROUP)
                    simpMessagingTemplate.convertAndSend("/messages/group." + input.getReceiverId(), SocketPayload.create(input, input.getAction()));
                else
                    simpMessagingTemplate.convertAndSend("/messages/user." + input.getReceiverId(), SocketPayload.create(input, input.getAction()));
                return CompletableFuture.completedFuture(null);
            }
            case CONSTS.EDIT_MESSAGE: {
                Message mess = messageRepository.findById(input.getMsgId()).orElse(null);
                if (mess == null)
                    throw new NotFoundException("Not found message");
                mess.getHistoryChanges().add(new HistoryChange(mess.getContent()));
                mess.setContent(input.getMessageText());
                mess = messageRepository.save(mess);
                input.setMessage(mess);
                if (input.getReceiverType() == CONSTS.MESSAGE_GROUP)
                    simpMessagingTemplate.convertAndSend("/messages/group." + input.getReceiverId(), SocketPayload.create(input, input.getAction()));
                else
                    simpMessagingTemplate.convertAndSend("/messages/user." + input.getReceiverId(), SocketPayload.create(input, input.getAction()));
                return CompletableFuture.completedFuture(mess);
            }
            case CONSTS.DELETE_MESSAGE: {
                Message mess = messageRepository.findById(input.getMsgId()).orElse(null);
                if (mess == null)
                    throw new NotFoundException("Not found message");
                mess.setDeleted(true);
                mess = messageRepository.save(mess);
                Message lastMessage = messageRepository.findLastMessageByCode(mess.getCode()).orElse(null);
                Conservation conservation = conservationRepository.findByCode(mess.getCode());
                conservation.setLastMessage(lastMessage);
                conservationRepository.save(conservation);
                input.setMessage(mess);
                if (input.getReceiverType() == CONSTS.MESSAGE_GROUP)
                    simpMessagingTemplate.convertAndSend("/messages/group." + input.getReceiverId(), SocketPayload.create(input, input.getAction()));
                else
                    simpMessagingTemplate.convertAndSend("/messages/user." + input.getReceiverId(), SocketPayload.create(input, input.getAction()));
                return CompletableFuture.completedFuture(mess);
            }
        }
        return CompletableFuture.completedFuture(null);
    }

    @Async
    @Override
    public CompletableFuture<Message> sendGroupMessage(String groupId, PayloadMessage messagePayload) {
        String userId = ((UserDetails) (((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getAttribute("user"))).getUsername();
        User user = userRepository.findById(userId).orElse(null);
        Group group = iGroupRepository.findById(groupId).orElse(null);
        Conservation conservation = user.getConservations().stream()
                .filter(x -> x.getGroupId().contains(groupId))
                .findFirst().orElse(null);
        if (user == null)
            throw new NotFoundException("Not found user");
        if (conservation == null)
            throw new NotFoundException("Not found Conservation");
        Message message = new Message(messagePayload.getCode(), messagePayload.getContent(), messagePayload.getType(), userId, groupId, null, messagePayload.getFile());
        message.setUserId_send(userId);
        message.setCode(conservation.getCode());
        message.setGroupId(conservation.getGroupId());
        message.setTYPE(CONSTS.MESSAGE_GROUP);
        conservation = conservationRepository.findByCode(message.getCode());
        conservation.setLastMessage(message);
        conservationRepository.save(conservation);
        message = messageRepository.save(message);
        simpMessagingTemplate.convertAndSend("/messages/group." + groupId, SocketPayload.create(message, CONSTS.MESSAGE_GROUP));
        return CompletableFuture.completedFuture(message);
    }

    @Override
    @Async
    public CompletableFuture<List<Message>> findMessagesWithPaginationAndSearch(long skip, int limit, String code, String content) {
        String userId = ((UserDetails) (((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getAttribute("user"))).getUsername();
        if (!userRepository.findById(userId).get().getConservations().stream().anyMatch(elem -> elem.getCode().contains(code)))
            throw new NotFoundException("Not Found Conservation!");
        List<Message> messages = messageRepository.findMessagesWithPaginationAndSearch(skip, limit, code, content);
        messages = messages.stream()
                .sorted(Comparator.comparing(Message::getCreateAt))
                .collect(Collectors.toList());
        return CompletableFuture.completedFuture(messages);
    }

    @Override
    @Async
    public CompletableFuture<PagedResultDto<Message>> findMessagesByIdUser(long skip, int limit, String content, String contactId) {
        String userId = ((UserDetails) (((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getAttribute("user"))).getUsername();
        Conservation conservation = null;
        for (Conservation tmp :
                userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Not found user")).getConservations()) {
            if (tmp.getUserId_1().equals(contactId) || tmp.getUserId_2().equals(contactId)) {
                conservation = tmp;
                break;
            }
        }
        ;
        if (conservation == null)
            return CompletableFuture.completedFuture(PagedResultDto.create(Pagination.create(0, skip, limit), new ArrayList<Message>()));

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("content").regex(content, "i").and("code").in(conservation.getCode())),
                Aggregation.sort(Sort.Direction.DESC, "createAt"),
                Aggregation.skip(skip),
                Aggregation.limit(limit)
        );
        List<Message> messages = mongoTemplate.aggregate(aggregation, "Message", Message.class).getMappedResults();
        messages = messages.stream()
                .sorted(Comparator.comparing(Message::getCreateAt))
                .collect(Collectors.toList());
        aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("content").regex(content, "i").and("code").in(conservation.getCode())),
                Aggregation.sort(Sort.Direction.DESC, "createAt")
        );
        long total = mongoTemplate.aggregate(aggregation, "Message", Message.class).getMappedResults().size();
        return CompletableFuture.completedFuture(PagedResultDto.create(Pagination.create(total, skip, limit), messages));
    }

    @Override
    @Async
    public CompletableFuture<Long> countMessagesByCode(String code, String content) {
        return CompletableFuture.supplyAsync(() -> messageRepository.countMessagesByCode(code, content).orElse(0L));
    }

    @Override
    @Async
    public CompletableFuture<Message> markAsDelivered(String code) {
        Message message = messageRepository.findByCode(code).orElse(null);
        if (message == null)
            throw new NotFoundException("Not Found Message!");
        message.setDeliveredAt(new Date());
        message = messageRepository.save(message);
        return CompletableFuture.completedFuture(message);
    }

    @Override
    @Async
    public CompletableFuture<List<Message>> getReplyMessage(String id) {
        Message message = messageRepository.findById(id).orElse(null);
        if (message == null)
            throw new NotFoundException("Not Found Message!");
        return CompletableFuture.completedFuture(message.getReplyMessages());
    }

    @Override
    @Async
    public CompletableFuture<Message> markAsRead(String code) {
        Message message = messageRepository.findByCode(code).orElse(null);
        if (message == null)
            throw new NotFoundException("Not Found Message!");
        message.setReadAt(new Date());
        message = messageRepository.save(message);
        return CompletableFuture.completedFuture(message);
    }

    /// Basic CRUD

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
}
