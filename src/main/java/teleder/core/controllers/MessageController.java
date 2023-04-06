package teleder.core.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;
import teleder.core.annotations.ApiPrefixController;
import teleder.core.annotations.Authenticate;
import teleder.core.dtos.PagedResultDto;
import teleder.core.dtos.Pagination;
import teleder.core.models.Message.Message;
import teleder.core.services.Message.IMessageService;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
@ApiPrefixController("messages")
public class MessageController {
    @Autowired
    IMessageService messageService;

    @Authenticate
    @MessageMapping("/privateMessage/{recipientId}")
    public void sendPrivateMessage(@DestinationVariable("recipientId") String recipientId,Message message) {
        messageService.sendPrivateMessage(recipientId, message);
    }

    @Authenticate
    @MessageMapping("/groupMessage/{groupId}")
    public void sendGroupMessage(@DestinationVariable("groupId") String groupId, @Payload Message message) {
        messageService.sendGroupMessage(groupId, message);
    }

    @Async
    @Authenticate
    @GetMapping("/{code}")
    public PagedResultDto<Message> findMessagesWithPaginationAndSearch(@RequestParam(name = "page", defaultValue = "0") int page,
                                                                       @RequestParam(name = "size", defaultValue = "10") int size,
                                                                       @RequestParam(name = "content", defaultValue = "") String content,
                                                                       @PathVariable(name = "code") String code) {

        CompletableFuture<Long> total = messageService.countMessagesByCode(code);
        CompletableFuture<List<Message>> messages = messageService.findMessagesWithPaginationAndSearch(page * size, size, code, content);
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(total, messages);
        try {
            allFutures.get();
            return PagedResultDto.create(Pagination.create(total.get(), page * size, size), messages.get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("Some thing went wrong!");
    }

    @Async
    @Authenticate
    @GetMapping("/message-by-code-paginate/{code}")
    public PagedResultDto<Message> findMessagesWithPaginationAndSearch(@RequestParam(name = "limit", defaultValue = "30") int limit,
                                                                       @RequestParam(name = "content", defaultValue = "") String content,
                                                                       @PathVariable(name = "code") String code) {

        CompletableFuture<Long> total = messageService.countMessagesByCode(code);
        CompletableFuture<List<Message>> messages = messageService.findMessagesWithPaginationAndSearch(0, limit, code, content);
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(total, messages);
        try {
            allFutures.get();
            return PagedResultDto.create(Pagination.create(total.get(), 0 , limit), messages.get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("Some thing went wrong!");
    }

    @Async
    @Authenticate
    @PatchMapping("/mark-as-read/{code}")
    public void markAsRead(@PathVariable(name = "code") String code) {
        messageService.markAsRead(code);
    }
    @Async
    @Authenticate
    @PatchMapping("/mark-as-delivered/{code}")
    public void markAsDelivered(@PathVariable(name = "code") String code) {
        messageService.markAsDelivered(code);
    }

}
