package teleder.core.controllers.auth.Dtos;

import lombok.Data;

@Data
public class PayLoadResetPasswordByPhone {
    String newPassword;
    String token;
}
