package teleder.core.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import teleder.core.config.ApiPrefixController;
import teleder.core.config.Authenticate;
import teleder.core.models.User.User;
import teleder.core.services.User.IUserService;
import teleder.core.services.User.dtos.CreateUserDto;
import teleder.core.services.User.dtos.UserDto;
import teleder.core.services.User.dtos.UserProfileDto;

import java.util.concurrent.CompletableFuture;

@RestController
@ApiPrefixController("users")
public class UserController {
    @Autowired
    IUserService userService;
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public CompletableFuture<UserDto> create(@RequestBody CreateUserDto input) {
        return userService.create(input);
    }

    @Authenticate
    @GetMapping(value = "/profile",produces = MediaType.APPLICATION_JSON_VALUE)
    public CompletableFuture<UserProfileDto> getProfile(HttpServletRequest request) {
        return userService.getProfile(request);
    }


}
