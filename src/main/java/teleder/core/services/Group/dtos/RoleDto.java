package teleder.core.services.Group.dtos;

import lombok.Data;
import teleder.core.models.Permission.Action;

import java.util.List;

@Data
public class RoleDto {
    String roleName;
    List<Action> permissions;
}
