package teleder.core.config;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import teleder.core.dtos.UserOnlineOfflinePayload;
import teleder.core.exceptions.NotFoundException;
import teleder.core.models.User.Contact;
import teleder.core.models.User.User;
import teleder.core.repositories.IUserRepository;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketEventListener {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);
    private Map<String, String> sessionIdToUsernameMap = new ConcurrentHashMap<>();
    @Autowired
    private SimpMessageSendingOperations messagingTemplate;
    @Autowired
    private IUserRepository userRepository;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String userId =
                (((Map<String, List<String>>) MessageHeaderAccessor.getAccessor(((Message<?>) headerAccessor.getHeader("simpConnectMessage")), StompHeaderAccessor.class).getHeader("nativeHeaders")).entrySet().iterator().next().getValue()).toString().replaceAll("\\[|\\]", "");

        if (userId == null)
            throw new NotFoundException("Not Found User");
        User user = userRepository.findById(userId).orElse(null);
        if (user == null)
            throw new NotFoundException("Not Found User");
        String sessionId = headerAccessor.getSessionId();
        sessionIdToUsernameMap.put(sessionId, user.getUsername());
        logger.info("User Connected: " + user.getDisplayName());
        user.setActive(true);
        user = userRepository.save(user);
        for (Contact contact : user.getList_contact()) {
            messagingTemplate.convertAndSend("/messages/user." + contact.getUser().getId(), new UserOnlineOfflinePayload(contact.getUser().getId(), true));
        }
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        String sessionId = event.getSessionId();
        String username = sessionIdToUsernameMap.get(sessionId);
        if (username != null) {
            User user = userRepository.findById(username).orElse(null);
            if (user == null)
                throw new NotFoundException("Not Found User");
            logger.info("User Disconnected: " + username);
            user.setActive(false);
            user.setLastActiveAt(new Date());
            user = userRepository.save(user);
            sessionIdToUsernameMap.remove(sessionId);
            for (Contact contact : user.getList_contact()) {
                messagingTemplate.convertAndSend("/messages/user." + contact.getUser().getId(), new UserOnlineOfflinePayload(contact.getUser().getId(), false));
            }
        }
    }
}
