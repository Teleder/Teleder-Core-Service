package teleder.core.dtos;

import lombok.Data;
import teleder.core.models.File.File;
import teleder.core.models.User.Contact;
import teleder.core.models.User.User;

import java.util.ArrayList;
import java.util.List;

@Data
public class UserConservationDto {
    private String id;
    private String displayName;
    private String bio;
    private File avatar;
    private File qr;
    private User.Role role;
    private List<Contact> list_contact = new ArrayList<>();
}
