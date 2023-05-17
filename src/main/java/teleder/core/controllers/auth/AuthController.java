package teleder.core.controllers.auth;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import teleder.core.annotations.ApiPrefixController;
import teleder.core.config.JwtTokenUtil;
import teleder.core.controllers.auth.Dtos.PayLoadResetPasswordByPhone;
import teleder.core.controllers.auth.Dtos.TokenDto;
import teleder.core.controllers.auth.Dtos.TokenResetPasswordDto;
import teleder.core.exceptions.BadRequestException;
import teleder.core.exceptions.NotFoundException;
import teleder.core.exceptions.UnauthorizedException;
import teleder.core.models.User.Contact;
import teleder.core.models.User.User;
import teleder.core.repositories.IUserRepository;
import teleder.core.services.SMS.SMSService;
import teleder.core.services.User.dtos.UserBasicDto;
import teleder.core.services.User.dtos.UserProfileDto;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Random;

@RestController
@ApiPrefixController("/auth")
public class AuthController {
    private final SMSService smsService;
    private final JwtTokenUtil jwtUtil;

    private final IUserRepository userRepository;

    private final ModelMapper toDto;
    final
    MongoTemplate mongoTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    //    @Value("${jwt.secret}")
    private String ENCRYPTION_KEY = "Jf57xtfgC5X9tktm"; // Thay đổi bằng khóa bí mật 16 ký tự của bạn
    private static final String AES_ALGORITHM = "AES";

    public AuthController(SMSService smsService, JwtTokenUtil jwtUtil, IUserRepository userRepository, ModelMapper toDto, MongoTemplate mongoTemplate) {
        this.smsService = smsService;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.toDto = toDto;
        this.mongoTemplate = mongoTemplate;
    }

    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody @Valid LoginInputDto loginRequest) throws Exception {
        final List<User> users = userRepository.findByPhoneAndEmail(loginRequest.getUsername());
        if (users.size() == 0)
            throw new NotFoundException("Cannot find user with email or phone");

        if (!JwtTokenUtil.comparePassword(loginRequest.getPassword(), users.get(0).getPassword())) {
            throw new UnauthorizedException("Password not correct");
        }
        final String accessToken = jwtUtil.generateAccessToken(users.get(0));
        final String refreshToken = jwtUtil.generateRefreshToken(users.get(0));
        toDto.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        for (Contact c : users.get(0).getList_contact()) {
            c.setUser(toDto.map(userRepository.findById(c.getUserId()).orElseThrow(() -> new NotFoundException("Cannot find user")), UserBasicDto.class));
        }
        return ResponseEntity.ok(new LoginDto(accessToken, refreshToken, toDto.map(users.get(0), UserProfileDto.class)));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshAuthenticationToken(@RequestBody @Valid RefreshTokenInput refreshTokenRequest) throws Exception {
        final String refreshToken = refreshTokenRequest.getRefreshToken();
        // Check if the refresh token is valid and not expired
        String id = jwtUtil.checkRefreshToken(refreshToken);
        if (id == null) {
            throw new BadRequestException("Not type refresh token");
        }
        final User user = userRepository.findById(id).orElse(null);
        if (user == null)
            throw new RuntimeException("Cannot find user with email or phone");
        if (jwtUtil.validateToken(refreshToken, user)) {
            final String accessToken = jwtUtil.generateAccessToken(user);
            return ResponseEntity.ok(new RefreshTokenDto(accessToken, refreshToken));
        }
        throw new Exception("Invalid refresh token");
    }
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPasswordByToken(@RequestBody PayLoadResetPasswordByPhone input) {
        User user = userRepository.findByTokenResetPassword(URLDecoder.decode(input.getToken())).orElseThrow(() -> new NotFoundException("User not found"));
        TokenResetPasswordDto tokenResetPassword = decodeToken(user.getTokenResetPassword());
        if (tokenResetPassword == null || tokenResetPassword.isExpired()) {
            return ResponseEntity.badRequest().body("Token is expired");
        }
        if (!tokenResetPassword.getEmail().equals(user.getEmail())) {
            return ResponseEntity.badRequest().body("Email not match!");
        }
        user.setRequestCount(0);
        user.setLastRequest(null);
        user.setPassword(JwtTokenUtil.hashPassword(input.getNewPassword()));
        userRepository.save(user);
        return ResponseEntity.ok(true);
    }
    @PostMapping("/request-pin/{phone}")
    public void requestPin(@PathVariable("phone") String phoneNumber) {
        User user = userRepository.findByPhoneNumber(phoneNumber).orElseThrow(() -> new NotFoundException("Phone not found"));
        TokenResetPasswordDto token = new TokenResetPasswordDto();
        token.setToken(generateRandomDigits());
        token.setEmail(user.getEmail());
        token.setExpired(LocalDateTime.now().plusMinutes(1).plusSeconds(2));
        token.setType(1);
        setToken(user, token);
        user = userRepository.save(user);
        String messageText = "Mã xác thực của bạn là: " + token.getToken();
        smsService.sendSMS(phoneNumber.replaceFirst("^0", "+84"), messageText);
    }
    @PostMapping("/validate-pin/{phone}")
    public ResponseEntity<?> validatePin(@PathVariable String phone, @RequestBody TokenDto input) throws UnsupportedEncodingException {
        User user = userRepository.findByPhoneNumber(phone).orElseThrow(() -> new NotFoundException("User not found"));
        TokenResetPasswordDto tokenResetPassword = decodeToken(user.getTokenResetPassword());
        if (tokenResetPassword == null || tokenResetPassword.isExpired()) {
            return ResponseEntity.badRequest().body("Token is expired");
        }
        if (!tokenResetPassword.getToken().equals(input.getToken())) {
            return ResponseEntity.badRequest().body("Pin code is not correct!");
        }
        tokenResetPassword.setExpired(LocalDateTime.now().plusMinutes(1).plusSeconds(2).plusSeconds(2));
        user.setTokenResetPassword(encodeToken(tokenResetPassword));
        userRepository.save(user);

        return ResponseEntity.ok(URLEncoder.encode(user.getTokenResetPassword(), "UTF-8"));
    }
    private String generateRandomDigits() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder(6);
        for (int i = 0; i < 6; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    private String encodeToken(TokenResetPasswordDto tokenResetPassword) {
        try {
            String jsonString = objectMapper.writeValueAsString(tokenResetPassword);
            byte[] encryptedBytes = encryptAES(jsonString.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            throw new RuntimeException("Error encoding token", e);
        }
    }

    private TokenResetPasswordDto decodeToken(String encodedToken) {
        try {
            byte[] decryptedBytes = decryptAES(Base64.getDecoder().decode(encodedToken));
            String jsonString = new String(decryptedBytes, StandardCharsets.UTF_8);
            return objectMapper.readValue(jsonString, TokenResetPasswordDto.class);
        } catch (Exception e) {
            throw new RuntimeException("Error decoding token", e);
        }
    }

    private byte[] encryptAES(byte[] data) throws Exception {
        Key key = new SecretKeySpec(ENCRYPTION_KEY.getBytes(StandardCharsets.UTF_8), AES_ALGORITHM);
        Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(data);
    }

    private byte[] decryptAES(byte[] encryptedData) throws Exception {
        Key key = new SecretKeySpec(ENCRYPTION_KEY.getBytes(StandardCharsets.UTF_8), AES_ALGORITHM);
        Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(encryptedData);
    }

    private void setToken(User user, TokenResetPasswordDto token) {
        user.setTokenResetPassword(encodeToken(token));
        if (user.getRequestCount() > 3
                && (user.getLastRequest() == null || user.getLastRequest().getTime() - new Date().getTime() < 24 * 60 * 60 * 1000)) {
            throw new BadRequestException("Bạn đã gửi quá nhiều yêu cầu để cấp lại mật khẩu, hãy đợi 24h sau để thực hiện lại chức năng");
        }
        user.setRequestCount(user.getRequestCount() + 1);
        user.setLastRequest(new Date(new Date().getTime()));
    }

}