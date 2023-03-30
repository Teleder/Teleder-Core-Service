package teleder.core.services.User;

import com.google.zxing.WriterException;
import jakarta.servlet.http.HttpServletRequest;
import teleder.core.dtos.BlockContactDto;
import teleder.core.dtos.PagedResultDto;
import teleder.core.models.User.Contact;
import teleder.core.services.IMongoService;
import teleder.core.services.User.dtos.CreateUserDto;
import teleder.core.services.User.dtos.UpdateUserDto;
import teleder.core.services.User.dtos.UserDto;
import teleder.core.services.User.dtos.UserProfileDto;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public interface IUserService extends IMongoService<UserDto, CreateUserDto, UpdateUserDto> {

    CompletableFuture<UserDto> create(CreateUserDto input) throws WriterException, IOException, ExecutionException, InterruptedException;

    CompletableFuture<UserProfileDto> getProfile(HttpServletRequest request);

    CompletableFuture<Boolean> addContact(String contactId);

    CompletableFuture<Boolean> blockContact(String contact_id, String reason);

    CompletableFuture<Boolean> removeContact(String contactId);

    CompletableFuture<Boolean> removeBlock(String contactId);

    public CompletableFuture<PagedResultDto<Contact>> getListContact(String displayName, long skip, int limit);

    public CompletableFuture<PagedResultDto<Contact>> getListContactWaitingAccept(String displayName, long skip, int limit);

    public CompletableFuture<Boolean> respondToRequestForContacts(String contact_id, Boolean accept);
    public CompletableFuture<List<Contact>> getListContactRequestSend();
}
