package teleder.core.services.Group.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import teleder.core.models.Conservation.Conservation;
import teleder.core.models.User.Block;

import java.util.List;

@Data
public class UpdateUserDto extends CreateUserDto {
    @JsonProperty(value = "avatar")
    private String avatar;
    @JsonProperty(value = "QR")
    private String QR;
    @JsonProperty(value = "list_block")

    private List<Block> list_block;
    @JsonProperty(value = "conservation")

    private List<Conservation> conservation;
}
