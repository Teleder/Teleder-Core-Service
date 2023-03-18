package teleder.core.model.User;

import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Document(collection = "User")
@Data
public class User {
    @Id
    private String id;
    private String firstName;
    private String lastName;
    private String phone;
    private String bio;
    private String avatar;
    private String QR;
    private List<Block> list_block;
    private List<Chat> code_chat;
//    List <Message> list_
    @DBRef
    private List<User> list_contact;
    @CreatedBy
    private Date createdAt = new Date();
    @LastModifiedDate
    private Date updatedAt = new Date();
    boolean isDeleted = false;
    public User() {
        this.firstName = "12";
    }
}
