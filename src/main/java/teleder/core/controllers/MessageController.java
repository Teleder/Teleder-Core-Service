package teleder.core.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import teleder.core.config.ApiPrefixController;
import teleder.core.config.Authenticate;
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
    @MessageMapping("/privateMessage")
    public void sendPrivateMessage(Message message) {
        messageService.sendPrivateMessage(message);
    }

    @Authenticate
    @MessageMapping("/groupMessage")
    public void sendGroupMessage(Message message) {
        messageService.sendGroupMessage(message);
    }

    @Async
    @Authenticate
    @GetMapping("/{code}")
    public PagedResultDto<Message> findMessagesWithPaginationAndSearch(@RequestParam(name = "page", defaultValue = "0") int page,
                                                                       @RequestParam(name = "size", defaultValue = "10") int size,
                                                                       @RequestParam(name = "content") String content,
                                                                       @PathVariable String code) {

        CompletableFuture<Long> total = messageService.countMessagesByCode(code);
        CompletableFuture<List<Message>> messages = messageService.findMessagesWithPaginationAndSearch(page * size, (page + 1) * size, code, content);
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(total, messages);
        try {
            allFutures.get();
            return PagedResultDto.create(Pagination.create(total.get(), page * size, (page + 1) * size), messages.get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("Some thing went wrong!");
    }

}
