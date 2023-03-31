package teleder.core.controllers.auth;


import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import teleder.core.annotations.ApiPrefixController;
import teleder.core.config.JwtTokenUtil;
import teleder.core.models.User.User;
import teleder.core.repositories.IUserRepository;
import teleder.core.services.User.dtos.UserProfileDto;

@RestController
@ApiPrefixController("/auth")
public class AuthController {

    @Autowired
    private JwtTokenUtil jwtUtil;

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private ModelMapper toDto;

    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody @Valid LoginInputDto loginRequest) throws Exception {
        final User user = userRepository.findByPhoneAndEmail(loginRequest.getUsername()).get(0);
        if (user == null) {
            throw new Exception("Cannot find user with email");
        }
        if (!JwtTokenUtil.comparePassword(loginRequest.getPassword(), user.getPassword())) {
            throw new Exception("Password not correct");
        }
        final String accessToken = jwtUtil.generateAccessToken(user);
        final String refreshToken = jwtUtil.generateRefreshToken(user);
        toDto.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        return ResponseEntity.ok(new LoginDto(accessToken, refreshToken, toDto.map(user, UserProfileDto.class)));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshAuthenticationToken(@RequestBody @Valid RefreshTokenInput refreshTokenRequest) throws Exception {
        final String refreshToken = refreshTokenRequest.getRefreshToken();
        // Check if the refresh token is valid and not expired
        final User userDetails = userRepository.findByPhoneAndEmail(jwtUtil.getUsernameFromToken(refreshToken)).get(0);
        if (jwtUtil.validateToken(refreshToken, userDetails)) {
            final String accessToken = jwtUtil.generateAccessToken(userDetails);
            return ResponseEntity.ok(new RefreshTokenDto(accessToken, refreshToken));
        }
        throw new Exception("Invalid refresh token");
    }

}