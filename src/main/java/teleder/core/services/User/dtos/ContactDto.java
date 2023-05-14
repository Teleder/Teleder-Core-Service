package teleder.core.services.User.dtos;

import lombok.Data;
import teleder.core.models.User.Contact;

@Data
public class ContactDto {
    UserBasicDto user;
    Contact.Status status;
}
