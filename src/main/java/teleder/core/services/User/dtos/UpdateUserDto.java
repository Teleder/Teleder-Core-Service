package teleder.core.services.User.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import teleder.core.models.Conservation.Conservation;
import teleder.core.models.File.File;
import teleder.core.models.User.Block;

import java.util.List;

@Data
public class UpdateUserDto  {
    private String firstName;
    @JsonProperty(value = "lastName", required = true)
    private String lastName;
    @JsonProperty(value = "phone", required = true)
    @Pattern(regexp ="^\\d{10,11}$", message = "Phone have 10 to 11 digit")
    private String phone;
    @JsonProperty(value = "avatar")
    private File avatar;
    @JsonProperty(value = "blocks")
    private List<Block> blocks;
}
