package teleder.core.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import teleder.core.config.ApiPrefixController;
import teleder.core.config.Authenticate;
import teleder.core.config.RequiresAuthorization;
import teleder.core.models.User.User;
import teleder.core.services.User.IUserService;
import teleder.core.services.User.dtos.CreateUserDto;
import teleder.core.services.User.dtos.UpdateUserDto;
import teleder.core.services.User.dtos.UserDto;
import teleder.core.services.User.dtos.UserProfileDto;

import java.util.List;
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
    @RequiresAuthorization("ADMIN")
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CompletableFuture<UserDto> getOne(@PathVariable String id) {
        return userService.getOne(id);
    }

    @Authenticate
    @RequiresAuthorization("ADMIN")
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public CompletableFuture<List<UserDto>> getAll() {
        return userService.getAll();
    }
    @Authenticate
    @RequiresAuthorization("ADMIN")
    @PatchMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CompletableFuture<UserDto> update(@PathVariable String id, @RequestBody UpdateUserDto input) {
        return userService.update(id, input);
    }

    @Authenticate
    @RequiresAuthorization("ADMIN")
    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CompletableFuture<Void> remove(@PathVariable String id) {
        return userService.delete(id);
    }
    @Authenticate
    @GetMapping(value = "/profile", produces = MediaType.APPLICATION_JSON_VALUE)
    public CompletableFuture<UserProfileDto> getProfile(HttpServletRequest request) {
        return userService.getProfile(request);
    }
    @Authenticate
    @PatchMapping(value = "/profile", produces = MediaType.APPLICATION_JSON_VALUE)
    public CompletableFuture<UserDto> updateProfile(HttpServletRequest request, @RequestBody UpdateUserDto input) {
        return userService.update(((User)request.getAttribute("user")).getId(), input);
    }
}
