package teleder.core.dtos;

import lombok.Data;
import teleder.core.models.Conservation.Conservation;
import teleder.core.models.Group.Group;
import teleder.core.models.Group.PinMessage;
import teleder.core.services.User.dtos.UserProfileDto;

import java.util.List;

@Data
public class ConservationDto {
    private String id;
    private String code;
    private String type;
    private List<PinMessage> pinMessage;
    private UserProfileDto user_1;
    private UserProfileDto user_2;
    private Group group;
}
