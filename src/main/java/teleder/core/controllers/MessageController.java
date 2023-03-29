package teleder.core.controllers;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.RestController;
import teleder.core.models.Message.Message;
import teleder.core.services.Message.IMessageService;

@RestController
public class MessageController {
    IMessageService messageService;
//    @MessageMapping("/group/message/{to}")
//    public Message sendMessage(@DestinationVariable String to, Message message) {
//        Message mess = messageService.handleActionInGroup(to, message);
//        return message;
//    }

//    @MessageMapping("/message/{to}")
//    public Message sendMessage(@DestinationVariable String to,  Message message) {
//        Message mess = messageService.handleActionSendMessage(to, message);
//        return message;
//    }

}
