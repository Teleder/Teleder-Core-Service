package teleder.core.models.File;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import teleder.core.models.BaseModel;
import teleder.core.models.User.User;

@Document(collection = "File")
@Data
public class File extends BaseModel {

    String name;
    FileCategory file_type;
    double file_size;
    String url;
    String code;
    @Id
    private String id;
    @DBRef
    private User user_own;

    public File(String name, FileCategory file_type, double file_size, String url, String code) {
        this.name = name;
        this.file_size = file_size;
        this.file_type = file_type;
        this.url = url;
        this.code = code;
    }
}
