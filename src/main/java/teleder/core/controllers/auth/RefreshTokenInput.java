package teleder.core.controllers.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RefreshTokenInput {

    @JsonProperty(value = "refreshToken", required = true)
    public String refreshToken;

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

}
