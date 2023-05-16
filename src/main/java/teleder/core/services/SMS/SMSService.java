package teleder.core.services.SMS;

import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SMSService {

    @Value("${twilio.phone_number}")
    private String twilioPhoneNumber;

    public void sendSMS(String toPhoneNumber, String messageText) {
        Message message = Message.creator(
                        new PhoneNumber(toPhoneNumber),
                        new PhoneNumber(twilioPhoneNumber),
                        messageText)
                .create();
    }
}
