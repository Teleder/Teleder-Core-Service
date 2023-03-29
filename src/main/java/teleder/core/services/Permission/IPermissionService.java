package teleder.core.services.Permission;

import teleder.core.services.IMongoService;
import teleder.core.services.Permission.dtos.CreatePermissionDto;
import teleder.core.services.Permission.dtos.PermissionDto;
import teleder.core.services.Permission.dtos.UpdatePermissionDto;

public interface IPermissionService extends IMongoService<PermissionDto, CreatePermissionDto, UpdatePermissionDto> {

}
