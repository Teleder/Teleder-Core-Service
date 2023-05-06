package teleder.core.services.User.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.joda.time.DateTime;
import teleder.core.models.File.File;

@Data
public class UserSearchDto {
    @JsonProperty(value = "createAt")
    public DateTime createAt;
    @JsonProperty(value = "updateAt")
    public DateTime updateAt;
    @JsonProperty(value = "id")
    private String id;
    @JsonProperty(value = "firstName", required = true)
    private String firstName;
    @JsonProperty(value = "lastName", required = true)
    private String lastName;
    @JsonProperty(value = "phone", required = true)
    private String phone;
    @JsonProperty(value = "bio")
    private String bio;
    @JsonProperty(value = "avatar")
    private File avatar;
}
