package teleder.core.model.Group;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.DBRef;
import teleder.core.model.Permission.Permission;
import java.util.List;
@Data
public class Role {
    String name;
    @DBRef
    private List<Permission> permissions ;
}
