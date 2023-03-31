package teleder.core.services.Group.dtos;

import lombok.Data;
import teleder.core.models.File.File;
import teleder.core.models.Group.Member;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class CreateGroupDto {
    List<Member> member = new ArrayList<>();
    boolean isPublic;
    private String code = UUID.randomUUID().toString();
    private String user_own_id;
    private File avatarGroup;
}
