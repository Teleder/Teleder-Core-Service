package teleder.core.services.User.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class CreateUserDto {
    @JsonProperty(value = "firstName", required = true)
    private String firstName;
    @JsonProperty(value = "lastName", required = true)
    private String lastName;
    @JsonProperty(value = "phone", required = true)
    @Pattern(regexp ="^\\d{10,11}$", message = "Phone have 10 to 11 digit")
    private String phone;
    @JsonProperty(value = "email")
    @NotBlank(message = "Email is mandatory")
    @Email(message = "Email should be valid")
    private String email;
    @JsonProperty(value = "bio")
    private String bio;
    @JsonProperty(value = "password")
    @NotBlank(message = "Password is mandatory")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[-+_!@#$%^&*.,?]).{6,16}$", message = "Password must be 6-16 characters long, with at least one special character, one lowercase letter, one uppercase letter, and one number")
    private String password;
}
