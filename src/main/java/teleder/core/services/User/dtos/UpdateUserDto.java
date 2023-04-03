package teleder.core.services.User.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import teleder.core.models.Conservation.Conservation;
import teleder.core.models.File.File;
import teleder.core.models.User.Block;

import java.util.List;

@Data
public class UpdateUserDto extends CreateUserDto {
    @JsonProperty(value = "avatar")
    private File avatar;
    @JsonProperty(value = "blocks")
    private List<Block> blocks;
    @JsonProperty(value = "conservation")
    private List<Conservation> conservation;
}
