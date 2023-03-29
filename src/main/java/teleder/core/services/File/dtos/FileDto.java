package teleder.core.services.File.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

@Data
public class FileDto extends UpdateFileDto {
    @JsonProperty(value = "createAt")
    public Date createAt;
    @JsonProperty(value = "updateAt")
    public Date updateAt;
    @JsonProperty(value = "id")
    private String id;

    @Override
    public String getPassword() {
        return null; // Ignore password validation
    }
}
