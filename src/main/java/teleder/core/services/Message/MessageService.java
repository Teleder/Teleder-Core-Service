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
import teleder.core.exceptions.BadRequestException;
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
        Conservation check = conservationRepository.findByCode(messagePayload.getCode());
        if (!check.isStatus())
            throw new BadRequestException("You cannot send message to this conservation");
        Message message = new Message(messagePayload.getContent(), messagePayload.getCode(), messagePayload.getType(), userId, contactId, null, messagePayload.getFile());
        if (user == null || contact == null)
            throw new NotFoundException("Not found user");
        message.setUserId_send(userId);
        message.setUserId_receive(contactId);
        message.setCode(messagePayload.getCode());
        if (messagePayload.getParentMessageId() == null) {
            message = messageRepository.save(message);
            Conservation conservation = null;
            List<Conservation> conservations = conservationRepository.getConservation(userId, contactId);
            if (conservations == null || conservations.size() == 0) {
                conservation = new Conservation(userId, message.getUserId_receive(), null);
                conservation = conservationRepository.save(conservation);
                user.getConservations().add(conservation.getId());
                contact.getConservations().add(conservation.getId());
                user.setConservations(user.getConservations());
                contact.setConservations(contact.getConservations());
                message.setCode(conservation.getCode());
                messageRepository.save(message);
                userRepository.save(user);
                userRepository.save(contact);
            }
            // add tin nhan vao db
            conservation = conservations.get(0);
            conservation.setUpdateAt(new Date());
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
        simpMessagingTemplate.convertAndSend("/messages/user." + user.getId(), SocketPayload.create(message, CONSTS.MESSAGE_PRIVATE));
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
                if (input.getReceiverType().equals(CONSTS.MESSAGE_GROUP))
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
                if (input.getReceiverType().equals(CONSTS.MESSAGE_GROUP))
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
        return CompletableFuture.completedFuture(input);
    }

    @Async
    @Override
    public CompletableFuture<Message> sendGroupMessage(String groupId, PayloadMessage messagePayload) {
        String userId = ((UserDetails) (((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getAttribute("user"))).getUsername();
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Not found user"));
        Group group = iGroupRepository.findById(groupId).orElse(null);
        Conservation conservation = conservationRepository.findByGroupId(groupId).orElseThrow(() -> new NotFoundException("Not found Conservation"));
        if (!conservation.isStatus())
            throw new BadRequestException("You cannot send message to this conservation");
        Message message = new Message(messagePayload.getContent(), messagePayload.getCode(), messagePayload.getType(), userId, groupId, null, messagePayload.getFile());
        message.setUserId_send(userId);
        message.setCode(conservation.getCode());
        message.setGroupId(conservation.getGroupId());
        message.setTYPE(CONSTS.MESSAGE_GROUP);
        conservation = conservationRepository.findByCode(message.getCode());
        message = messageRepository.save(message);
        conservation.setLastMessage(message);
        conservation.setUpdateAt(new Date());
        conservationRepository.save(conservation);
        simpMessagingTemplate.convertAndSend("/messages/group." + groupId, SocketPayload.create(message, CONSTS.MESSAGE_GROUP));
        return CompletableFuture.completedFuture(message);
    }

    @Override
    @Async
    public CompletableFuture<List<Message>> findMessagesWithPaginationAndSearch(long skip, int limit, String code, String content) {
        String userId = ((UserDetails) (((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getAttribute("user"))).getUsername();
        Conservation conservation = conservationRepository.findByCode(code);
        if (userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Cannot find user"))
                .getConservations().stream().noneMatch(elem -> conservation.getCode().contains(code)))
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

        List<Conservation> conservation = conservationRepository.getConservation(userId, contactId);
        if (conservation == null || conservation.size() == 0)
            return CompletableFuture.completedFuture(PagedResultDto.create(Pagination.create(0, skip, limit), new ArrayList<Message>()));

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("content").regex(content, "i").and("code").in(conservation.get(0).getCode())),
                Aggregation.sort(Sort.Direction.DESC, "createAt"),
                Aggregation.skip(skip),
                Aggregation.limit(limit)
        );
        List<Message> messages = mongoTemplate.aggregate(aggregation, "Message", Message.class).getMappedResults();
        messages = messages.stream()
                .sorted(Comparator.comparing(Message::getCreateAt))
                .collect(Collectors.toList());
        aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("content").regex(content, "i").and("code").in(conservation.get(0).getCode())),
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
