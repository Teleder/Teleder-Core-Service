package teleder.core.services.Group.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

@Data
public class UserDto extends UpdateUserDto {
    @JsonProperty(value = "id")
    private String id;
    @JsonProperty(value = "createAt")
    public Date createAt ;
    @JsonProperty(value = "updateAt")
    public Date updateAt ;
    @Override
    public String getPassword() {
        return null; // Ignore password validation
    }
}
