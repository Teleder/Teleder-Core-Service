package teleder.core.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import teleder.core.annotations.ApiPrefixController;
import teleder.core.annotations.Authenticate;
import teleder.core.dtos.PagedResultDto;
import teleder.core.dtos.Pagination;
import teleder.core.dtos.PayloadAction;
import teleder.core.dtos.PayloadMessage;
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
    @PostMapping("/privateMessage/{recipientId}")
    public  CompletableFuture<Message> sendPrivateMessage(@PathVariable("recipientId") String recipientId,@RequestBody PayloadMessage message) {
        return messageService.sendPrivateMessage(recipientId, message);
    }
    @Authenticate
    @PostMapping("/sendAction")
    public CompletableFuture<Void> sendAction(@RequestBody  PayloadAction input) {
        return messageService.sendAction(input);
    }
    @Authenticate
    @PostMapping("/groupMessage/{groupId}")
    public  CompletableFuture<Message> sendGroupMessage(@PathVariable("groupId") String groupId, @RequestBody PayloadMessage message) {
        return messageService.sendGroupMessage(groupId, message);
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
    public PagedResultDto<Message> findMessagesByCodePaginate( @RequestParam(name = "skip", defaultValue = "0") int skip,
                                                                        @RequestParam(name = "limit", defaultValue = "30") int limit,
                                                                       @RequestParam(name = "content", defaultValue = "") String content,
                                                                       @PathVariable(name = "code") String code) {

        CompletableFuture<Long> total = messageService.countMessagesByCode(code);
        CompletableFuture<List<Message>> messages = messageService.findMessagesWithPaginationAndSearch(skip, limit, code, content);
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
