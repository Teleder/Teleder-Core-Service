package teleder.core.models.User;

import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import teleder.core.models.Conservation.Conservation;
import teleder.core.models.File.File;

import java.util.Collection;
import java.util.Date;
import java.util.List;

@Document(collection = "User")
@Data
public class User implements UserDetails {
    @Id
    private String id;
    private String firstName;
    private String lastName;
    private String phone;
    private String email;
    private String bio;
    @DBRef
    private File avatar;
    private String QR;
    private List<Block> list_block;
    private String password;
    private List<Conservation> conservations;
    private Role role = Role.USER;
    //    List <Message> list_
    private List<Contact> list_contact;
    @CreatedBy
    private Date createAt = new Date();
    @LastModifiedDate
    private Date updateAt = new Date();
    boolean isDeleted = false;

    public User() {
        this.firstName = "12";
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
        return null;
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
