package teleder.core.services.Group.dtos;

import lombok.Data;
import teleder.core.models.File.File;
import teleder.core.models.Group.Member;

import java.util.*;

@Data
public class CreateGroupDto {
    List<Member> member = new ArrayList<>();
    boolean isPublic;
    private String avatarGroup;
    private String bio;
    private String name;
}
