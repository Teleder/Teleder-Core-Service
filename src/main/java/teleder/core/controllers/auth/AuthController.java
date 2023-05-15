package teleder.core.controllers.auth;


import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import teleder.core.annotations.ApiPrefixController;
import teleder.core.config.JwtTokenUtil;
import teleder.core.exceptions.BadRequestException;
import teleder.core.exceptions.NotFoundException;
import teleder.core.exceptions.UnauthorizedException;
import teleder.core.models.Conservation.Conservation;
import teleder.core.models.User.Contact;
import teleder.core.models.User.User;
import teleder.core.repositories.IUserRepository;
import teleder.core.services.User.dtos.UserBasicDto;
import teleder.core.services.User.dtos.UserProfileDto;

import java.util.List;

import static teleder.core.utils.PopulateDocument.populateConservation;

@RestController
@ApiPrefixController("/auth")
public class AuthController {

    private final JwtTokenUtil jwtUtil;

    private final IUserRepository userRepository;

    private final ModelMapper toDto;
    final
    MongoTemplate mongoTemplate;

    public AuthController(JwtTokenUtil jwtUtil, IUserRepository userRepository, ModelMapper toDto, MongoTemplate mongoTemplate) {
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
        for (Conservation con : users.get(0).getConservations()) {
            populateConservation(mongoTemplate, con );
        }
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
        if(id == null){
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

}