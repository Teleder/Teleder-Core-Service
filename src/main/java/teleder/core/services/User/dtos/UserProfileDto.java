package teleder.core.services.User.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import teleder.core.dtos.ConservationDto;
import teleder.core.models.Conservation.Conservation;
import teleder.core.models.File.File;
import teleder.core.models.User.Block;
import teleder.core.models.User.User;

import java.util.ArrayList;
import java.util.List;

@Data
public class UserProfileDto {
    @JsonProperty(value = "id")
    private String id;
    @JsonProperty(value = "firstName")
    private String firstName;
    @JsonProperty(value = "lastName")
    private String lastName;
    @JsonProperty(value = "displayName")
    private String displayName;
    @JsonProperty(value = "phone")
    private String phone;
    @JsonProperty(value = "email")
    private String email;
    @JsonProperty(value = "bio")
    private String bio;
    @JsonProperty(value = "avatar")
    private File avatar;
    @JsonProperty(value = "qr")
    private File qr;
    @JsonProperty(value = "blocks")
    private List<Block> blocks;
    @JsonProperty(value = "conservation")
    private List<Conservation> conservations = new ArrayList<>();
    @JsonProperty(value = "role")
    private User.Role role;
}
