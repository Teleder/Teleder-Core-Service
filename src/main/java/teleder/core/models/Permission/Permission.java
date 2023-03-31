package teleder.core.models.Permission;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import teleder.core.models.BaseModel;

@Document(collection = "Permission")
@Data
public class Permission extends BaseModel {
    Action action;
    @Id
    private String id;
}
