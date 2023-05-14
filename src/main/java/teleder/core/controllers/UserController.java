package teleder.core.controllers;

import com.google.zxing.WriterException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import teleder.core.annotations.ApiPrefixController;
import teleder.core.annotations.Authenticate;
import teleder.core.annotations.RequiresAuthorization;
import teleder.core.dtos.PagedResultDto;
import teleder.core.models.User.Contact;
import teleder.core.services.User.IUserService;
import teleder.core.services.User.dtos.*;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
@ApiPrefixController("users")
public class UserController {
    @Autowired
    IUserService userService;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public CompletableFuture<UserDto> create(@RequestBody CreateUserDto input) throws IOException, ExecutionException, InterruptedException, WriterException {
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
    public CompletableFuture<UserDto> update(@PathVariable String id, @RequestBody UpdateUserDto input) throws InvocationTargetException, IllegalAccessException {
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
    public CompletableFuture<UserProfileDto> getProfile() {
        String userId = ((UserDetails) (((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getAttribute("user"))).getUsername();
        ;
        return userService.getProfile(userId);
    }

    @Authenticate
    @PatchMapping(value = "/profile", produces = MediaType.APPLICATION_JSON_VALUE)
    public CompletableFuture<UserDto> updateProfile(@RequestBody UpdateUserDto input) throws InvocationTargetException, IllegalAccessException {
        String userId = ((UserDetails) (((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getAttribute("user"))).getUsername();
        return userService.update(userId, input);
    }

    @Authenticate
    @PatchMapping(value = "/add-contact", produces = MediaType.APPLICATION_JSON_VALUE)
    CompletableFuture<Boolean> addContact(@RequestParam("contactId") String contactId) {
        return userService.addContact(contactId);
    }

    @Authenticate
    @PatchMapping(value = "/block-contact", produces = MediaType.APPLICATION_JSON_VALUE)
    CompletableFuture<Boolean> blockContact(@RequestParam("contactId") String contactId, @RequestParam("reason") String reason) {
        return userService.blockContact(contactId, reason);
    }

    @Authenticate
    @PatchMapping(value = "/remove-contact", produces = MediaType.APPLICATION_JSON_VALUE)
    CompletableFuture<Boolean> removeContact(@RequestParam("contactId") String contactId) {
        return userService.removeContact(contactId);
    }

    @Authenticate
    @PatchMapping(value = "/remove-block", produces = MediaType.APPLICATION_JSON_VALUE)
    CompletableFuture<Boolean> removeBlock(@RequestParam("contactId") String contactId) {
        return userService.removeBlock(contactId);
    }

    @Authenticate
    @GetMapping(value = "/contacts", produces = MediaType.APPLICATION_JSON_VALUE)
    public CompletableFuture<PagedResultDto<UserBasicDto>> getListContact(@RequestParam(name = "displayName", defaultValue = "") String displayName,
                                                                          @RequestParam(name = "page", defaultValue = "0") int page,
                                                                          @RequestParam(name = "size", defaultValue = "10") int size) {
        return userService.getListContact(displayName, page * size, size);
    }

    @Authenticate
    @GetMapping(value = "/contact-waiting-accept", produces = MediaType.APPLICATION_JSON_VALUE)
    public CompletableFuture<PagedResultDto<Contact>> getListContactWaitingAccept(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        return userService.getListContactWaitingAccept(page * size, size);
    }

    @Authenticate
    @PatchMapping(value = "/respond-to-request-for-contacts", produces = MediaType.APPLICATION_JSON_VALUE)
    public CompletableFuture<Boolean> respondToRequestForContacts(@RequestParam("contactId") String contactId, @RequestParam Boolean accept) {
        return userService.responseToRequestForContacts(contactId, accept);
    }

    @Authenticate
    @GetMapping(value = "/contact-request-send", produces = MediaType.APPLICATION_JSON_VALUE)
    public CompletableFuture<List<Contact>> getListContactRequestSend() {
        return userService.getListContactRequestSend();
    }

    @Authenticate
    @PatchMapping(value = "/remove-request-friend", produces = MediaType.APPLICATION_JSON_VALUE)
    public CompletableFuture<UserProfileDto> removeRequestFriend( String contactId) {
        String userId = ((UserDetails) (((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getAttribute("user"))).getUsername();
        return userService.removeRequestFriend(userId, contactId);
    }

    @Authenticate
    @GetMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public CompletableFuture<List<UserBasicDto>> searchUser(@RequestParam String searchText) {
        return userService.searchUser(searchText);
    }
}
