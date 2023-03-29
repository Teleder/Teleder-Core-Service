package teleder.core.services.User;

import jakarta.servlet.http.HttpServletRequest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import teleder.core.config.JwtTokenUtil;
import teleder.core.models.User.User;
import teleder.core.repositories.IUserRepository;
import teleder.core.services.User.dtos.CreateUserDto;
import teleder.core.services.User.dtos.UserDto;
import teleder.core.services.User.dtos.UserProfileDto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class UserService implements IUserService, UserDetailsService {
    @Autowired
    IUserRepository userRepository;
    @Autowired
    private ModelMapper toDto;
    @Autowired
    private JwtTokenUtil jwtUtil;

    @Async
    public CompletableFuture<UserDto> create(CreateUserDto input) {
        User user = toDto.map(input, User.class);
        user.setPassword(jwtUtil.hashPassword(user.getPassword()));
        return CompletableFuture.completedFuture(toDto.map(userRepository.insert(user), UserDto.class));
    }

    @Override
    @Async
    public CompletableFuture<UserProfileDto> getProfile(HttpServletRequest request) {
        User user = userRepository.findByEmail(((User) request.getAttribute("user")).getEmail());
        return CompletableFuture.completedFuture(toDto.map(user, UserProfileDto.class));
    }

    @Override
    @Async
    public CompletableFuture<User> getOne(String id) {
        return null;
    }

    @Override
    @Async
    public CompletableFuture<List<User>> getAll() {
        return null;
    }

    @Override
    @Async
    public CompletableFuture<User> update(String id, User User) {
        return null;
    }

    @Override
    public void delete(String id) {

    }

    @Override
    public UserDetails loadUserByUsername(String input) throws UsernameNotFoundException {
        User user = userRepository.findByPhoneAndEmail(input);

        if (user == null) {
            throw new UsernameNotFoundException("User not found with email or phone: " + input);
        } else {
            Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority(user.getRole()));
            return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorities);
        }
    }
}
