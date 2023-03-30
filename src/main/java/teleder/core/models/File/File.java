package teleder.core.models.File;

import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import teleder.core.models.User.User;

import java.util.Date;

@Document(collection = "File")
@Data
public class File {

    @Id
    private String id;
    String name;
    FileCategory file_type;
    double file_size;
    String url;
    String code;
    @DBRef
    private User user_own;
    boolean isDeleted = false;
    @CreatedBy
    private Date createAt = new Date();
    @LastModifiedDate
    private Date updateAt = new Date();

    public File(String name, FileCategory file_type, double file_size, String url, String code) {
        this.name = name;
        this.file_size = file_size;
        this.file_type = file_type;
        this.url = url;
        this.code = code;
    }
}
