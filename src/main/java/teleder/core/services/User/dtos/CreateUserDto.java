package teleder.core.services.User.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CreateUserDto {
    @NotEmpty(message = "First Name  is required")
    private String firstName;
    @NotEmpty(message = "Last Name is required")
    @JsonProperty(value = "lastName", required = true)
    private String lastName;
    @NotEmpty(message = "Phone is required")
    @JsonProperty(value = "phone", required = true)
    @Pattern(regexp ="^\\d{10,11}$", message = "Phone have 10 to 11 digit")
    private String phone;
    @JsonProperty(value = "email")
    @NotEmpty(message = "Email is mandatory")
    @Email(message = "Email should be valid")
    private String email;
    @NotEmpty(message = "Bio is required")
    @JsonProperty(value = "bio")
    private String bio;
    @JsonProperty(value = "password")
    @NotEmpty(message = "Password is mandatory")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[-+_!@#$%^&*.,?]).{6,16}$", message = "Password must be 6-16 characters long, with at least one special character, one lowercase letter, one uppercase letter, and one number")
    private String password;
}
