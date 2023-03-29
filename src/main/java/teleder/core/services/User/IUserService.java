package teleder.core.services.User;

import jakarta.servlet.http.HttpServletRequest;
import teleder.core.services.IMongoService;
import teleder.core.services.User.dtos.CreateUserDto;
import teleder.core.services.User.dtos.UpdateUserDto;
import teleder.core.services.User.dtos.UserDto;
import teleder.core.services.User.dtos.UserProfileDto;

import java.util.concurrent.CompletableFuture;

public interface IUserService extends IMongoService<UserDto, CreateUserDto, UpdateUserDto> {

    CompletableFuture<UserDto> create(CreateUserDto input);
    CompletableFuture<UserProfileDto> getProfile(HttpServletRequest request);
     CompletableFuture<Boolean> addContact( String contactId);
    CompletableFuture<Boolean> blockContact( String contactId, String reason);
    CompletableFuture<Boolean> removeContact( String contactId);
    CompletableFuture<Boolean> removeBlock( String contactId);
}
