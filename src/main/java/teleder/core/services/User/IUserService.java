package teleder.core.services.User;

import com.google.zxing.WriterException;
import jakarta.servlet.http.HttpServletRequest;
import teleder.core.dtos.BlockContactDto;
import teleder.core.dtos.PagedResultDto;
import teleder.core.models.User.Contact;
import teleder.core.models.User.User;
import teleder.core.services.IMongoService;
import teleder.core.services.User.dtos.*;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public interface IUserService extends IMongoService<UserDto, CreateUserDto, UpdateUserDto> {

    CompletableFuture<UserDto> create(CreateUserDto input) throws WriterException, IOException, ExecutionException, InterruptedException;

    CompletableFuture<UserProfileDto> getProfile(String id);

    CompletableFuture<Boolean> addContact(String contactId);

    CompletableFuture<Boolean> blockContact(String contact_id, String reason);

    CompletableFuture<Boolean> removeContact(String contactId);

    CompletableFuture<Boolean> removeBlock(String contactId);

    public CompletableFuture<PagedResultDto<Contact>> getListContact(String displayName, long skip, int limit);

    public CompletableFuture<PagedResultDto<Contact>> getListContactWaitingAccept( long skip, int limit);

    public CompletableFuture<Boolean> responseToRequestForContacts(String contact_id, Boolean accept);

    public CompletableFuture<List<Contact>> getListContactRequestSend();
    public CompletableFuture<List<UserSearchDto>> searchUser(String searchText);
}
