package teleder.core.models.User;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import teleder.core.models.BaseModel;
import teleder.core.models.Conservation.Conservation;
import teleder.core.models.File.File;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Document(collection = "User")
@Data
public class User extends BaseModel implements UserDetails {
    @Id
    private String id;
    private String firstName;
    private String lastName;
    private String displayName;
    private String phone;
    private String email;
    private String bio;
    @DBRef
    private File avatar;
    @DBRef
    private File qr;
    private List<Block> blocks = new ArrayList<>();
    private String password;

    @DBRef(lazy = true)
    private List<Conservation> conservations = new ArrayList<>();

    private Role role = Role.USER;
    private List<Contact> list_contact = new ArrayList<>();
    boolean isActive = false;
    public User() {
        // Hàm tạo không đối số
    }
    public User(String id, String firstName, String lastName, String displayName, String bio, File avatar, File qr) {
        this.id = id;
        this.firstName = firstName;
        this.lastName =lastName;
        this.displayName = displayName;
        this.bio = bio;
        this.avatar = avatar;
        this.qr = qr;
    }

    public String getRole() {
        return role.name();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return this.id;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public enum Role {
        ADMIN,
        USER
    }
}
