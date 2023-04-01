package teleder.core.dtos;

import lombok.Data;
import teleder.core.models.File.File;
import teleder.core.models.User.User;

@Data
public class ContactInfoDto
{
    String id;
    String displayName;
    File avatar;
    public ContactInfoDto(User contact){
        id = contact.getId();
        displayName = contact.getDisplayName();
        avatar = contact.getAvatar();
    }
}
