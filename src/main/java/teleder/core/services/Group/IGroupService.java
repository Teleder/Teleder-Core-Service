package teleder.core.services.Group;

import teleder.core.services.Group.dtos.CreateGroupDto;
import teleder.core.services.Group.dtos.GroupDto;
import teleder.core.services.Group.dtos.UpdateGroupDto;
import teleder.core.services.IMongoService;

public interface IGroupService extends IMongoService<GroupDto, CreateGroupDto, UpdateGroupDto> {

}
