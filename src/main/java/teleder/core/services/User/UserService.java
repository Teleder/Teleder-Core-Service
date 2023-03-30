package teleder.core.services.User;

import jakarta.servlet.http.HttpServletRequest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import teleder.core.config.JwtTokenUtil;
import teleder.core.exceptions.NotFoundException;
import teleder.core.models.User.Block;
import teleder.core.models.User.Contact;
import teleder.core.models.User.User;
import teleder.core.repositories.IUserRepository;
import teleder.core.services.User.dtos.CreateUserDto;
import teleder.core.services.User.dtos.UpdateUserDto;
import teleder.core.services.User.dtos.UserDto;
import teleder.core.services.User.dtos.UserProfileDto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
public class UserService implements IUserService, UserDetailsService {
    @Autowired
    IUserRepository userRepository;
    @Autowired
    private ModelMapper toDto;


    @Override
    @Async
    public CompletableFuture<UserDto> create(CreateUserDto input) {
        User user = toDto.map(input, User.class);
        user.setPassword(JwtTokenUtil.hashPassword(user.getPassword()));
        return CompletableFuture.completedFuture(toDto.map(userRepository.insert(user), UserDto.class));
    }

    @Override
    @Async
    public CompletableFuture<UserProfileDto> getProfile(HttpServletRequest request) {
        User user = userRepository.findByPhoneAndEmail(((User) request.getAttribute("user")).getEmail()).get(0);
        return CompletableFuture.completedFuture(toDto.map(user, UserProfileDto.class));
    }

    @Override
    public CompletableFuture<Boolean> addContact(String contactId) {
        String userId = ((User) (((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getAttribute("user"))).getId();
        Optional<User> userOptional = userRepository.findById(userId);
        Optional<User> contactOptional = userRepository.findById(contactId);

        if (userOptional.isPresent() && contactOptional.isPresent()) {
            User user = userOptional.get();
            User contact = contactOptional.get();
            user.getList_contact().add(new Contact(contact, Contact.Status.WAITING));
            userRepository.save(user);
            return CompletableFuture.completedFuture(true);
        }
        throw new NotFoundException("Not Found Contact!");
    }

    @Override
    public CompletableFuture<Boolean> blockContact(String contactId, String reason) {
        String userId = ((User) (((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getAttribute("user"))).getId();
        Optional<User> userOptional = userRepository.findById(userId);
        Optional<User> contactOptional = userRepository.findById(contactId);
        if (userOptional.isPresent() && contactOptional.isPresent()) {
            User user = userOptional.get();
            User contact = contactOptional.get();
            user.getList_block().add(new Block(contact, reason));
            userRepository.save(user);
            return CompletableFuture.completedFuture(true);
        }
        throw new NotFoundException("Not Found Contact!");
    }

    @Override
    public CompletableFuture<Boolean> removeContact(String contactId) {
        String userId = ((User) (((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getAttribute("user"))).getId();
        Optional<User> userOptional = userRepository.findById(userId);
        Optional<User> contactOptional = userRepository.findById(contactId);

        if (userOptional.isPresent() && contactOptional.isPresent()) {
            User user = userOptional.get();
            User contact = contactOptional.get();
            Contact contactToRemove = null;
            for (Contact cont : user.getList_contact()) {
                if (cont.getUser().getId().equals(contact.getId())) {
                    contactToRemove = cont;
                    break;
                }
            }
            if (contactToRemove != null) {
                user.getList_block().remove(contactToRemove);
                userRepository.save(user);
            }
            return CompletableFuture.completedFuture(true);
        }
        throw new NotFoundException("Not Found Contact!");
    }

    @Override
    public CompletableFuture<Boolean> removeBlock(String contactId) {
        String userId = ((User) (((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getAttribute("user"))).getId();
        Optional<User> userOptional = userRepository.findById(userId);
        Optional<User> contactOptional = userRepository.findById(contactId);

        if (userOptional.isPresent() && contactOptional.isPresent()) {
            User user = userOptional.get();
            User contact = contactOptional.get();

            Block blockToRemove = null;
            for (Block block : user.getList_block()) {
                if (block.getUser().getId().equals(contact.getId())) {
                    blockToRemove = block;
                    break;
                }
            }
            if (blockToRemove != null) {
                user.getList_block().remove(blockToRemove);
                userRepository.save(user);
            }

            return CompletableFuture.completedFuture(true);
        }
        throw new NotFoundException("Not Found Contact!");
    }

    @Override
    @Async
    public CompletableFuture<UserDto> getOne(String id) {
        return CompletableFuture.completedFuture(toDto.map(userRepository.findById(id), UserDto.class));
    }

    @Override
    @Async
    public CompletableFuture<List<UserDto>> getAll() {
        return CompletableFuture.completedFuture(userRepository.findAll().stream().map(x -> toDto.map(x, UserDto.class)).toList());
    }

    @Override
    @Async
    public CompletableFuture<UserDto> update(String id, UpdateUserDto User) {
        User existingUserLevel = userRepository.findById(id).orElse(null);
        if (existingUserLevel == null)
            throw new NotFoundException("Unable to find user level!");
        BeanUtils.copyProperties(User, existingUserLevel);
        return CompletableFuture.completedFuture(toDto.map(userRepository.save(existingUserLevel), UserDto.class));
    }

    @Override
    @Async
    public CompletableFuture<Void> delete(String id) {
        User existingUserLevel = userRepository.findById(id).orElse(null);
        if (existingUserLevel == null)
            throw new NotFoundException("Unable to find user!");
        existingUserLevel.setDeleted(true);
        userRepository.save(existingUserLevel);
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public UserDetails loadUserByUsername(String input) throws UsernameNotFoundException {
        User user = userRepository.findByPhoneAndEmail(input).get(0);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with email or phone: " + input);
        } else {
            Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority(user.getRole()));
            return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorities);
        }
    }
}
