package teleder.core.services.User.dtos;

import lombok.Data;
import teleder.core.models.User.Contact;
import teleder.core.models.User.User;

@Data
public class ContactDto {
    UserSearchDto user;
    Contact.Status status;
}
