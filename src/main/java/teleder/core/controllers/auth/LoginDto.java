package teleder.core.controllers.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import teleder.core.services.User.dtos.UserProfileDto;

@Data
public class LoginDto {

    @JsonProperty(value = "accessToken", required = true)
    public String accessToken;
    @JsonProperty(value = "refreshToken", required = true)
    public String refreshToken;
    @JsonProperty(value = "UserProfileDto", required = true)
    public UserProfileDto user;

    public LoginDto(String accessToken, String refreshToken, UserProfileDto user) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.user = user;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public UserProfileDto getUser() {
        return user;
    }

    public void setUser(UserProfileDto user) {
        this.user = user;
    }

}
