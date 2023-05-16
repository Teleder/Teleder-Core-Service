package teleder.core.services.User.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.joda.time.DateTime;
import teleder.core.models.File.File;

import java.util.Date;

@Data
public class UserBasicDto {
    @JsonProperty(value = "createAt")
    public DateTime createAt;
    @JsonProperty(value = "updateAt")
    public DateTime updateAt;
    @JsonProperty(value = "id")
    private String id;
    @JsonProperty(value = "firstName", required = true)
    private String firstName;
    @JsonProperty(value = "displayName", required = true)
    private String displayName;
    @JsonProperty(value = "lastName", required = true)
    private String lastName;
    @JsonProperty(value = "phone", required = true)
    private String phone;
    @JsonProperty(value = "bio")
    private String bio;
    @JsonProperty(value = "avatar")
    private File avatar;
    @JsonProperty(value = "isActive")
    public boolean isActive = false;
    @JsonProperty(value = "lastActiveAt")
    Date lastActiveAt = new Date();
}
