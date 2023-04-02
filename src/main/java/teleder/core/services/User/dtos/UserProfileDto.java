package teleder.core.services.User.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import teleder.core.models.Conservation.Conservation;
import teleder.core.models.File.File;
import teleder.core.models.User.Block;
import teleder.core.models.User.User;

import java.util.List;

@Data
public class UserProfileDto {
    @JsonProperty(value = "id")
    private String id;
    @JsonProperty(value = "firstName")
    private String firstName;
    @JsonProperty(value = "lastName")
    private String lastName;
    @JsonProperty(value = "phone")
    private String phone;
    @JsonProperty(value = "email")
    private String email;
    @JsonProperty(value = "bio")
    private String bio;
    @JsonProperty(value = "avatar")
    private File avatar;
    @JsonProperty(value = "QR")
    private File QR;
    @JsonProperty(value = "list_block")
    private List<Block> list_block;
    @JsonProperty(value = "conservation")
    private List<Conservation> conservation;
    @JsonProperty(value = "role")
    private User.Role role;
}
