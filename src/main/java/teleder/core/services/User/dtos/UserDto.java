package teleder.core.services.User.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.joda.time.DateTime;

import java.util.Date;

@Data
public class UserDto extends UpdateUserDto {
    @JsonProperty(value = "createAt")
    public DateTime createAt;
    @JsonProperty(value = "updateAt")
    public DateTime updateAt;
    @JsonProperty(value = "id")
    private String id;

    @Override
    public String getPassword() {
        return null; // Ignore password validation
    }
}
