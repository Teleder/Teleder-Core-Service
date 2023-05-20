package teleder.core.services.Conservation.dtos;

import lombok.Data;
import org.springframework.data.annotation.Transient;
import teleder.core.services.User.dtos.UserBasicDto;

@Data
public class ConservationPrivateDto {
    private String userId_1;
    private String userId_2;
}
