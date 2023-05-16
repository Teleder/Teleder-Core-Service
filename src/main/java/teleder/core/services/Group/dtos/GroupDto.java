package teleder.core.services.Group.dtos;

import lombok.Data;
import teleder.core.models.Group.Block;
import teleder.core.models.Group.Member;
import teleder.core.models.Group.Role;
import teleder.core.models.Message.Message;
import teleder.core.models.User.User;
import teleder.core.services.User.dtos.UserBasicDto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class GroupDto extends UpdateGroupDto {
    private String code;
    String QR;
    List<Role> roles = new ArrayList<>();
    List<Member> members = new ArrayList<>();
    List<Block> block_list = new ArrayList<>();
    private List<Message> pinMessage = new ArrayList<>();
    private String id;
    private UserBasicDto user_own;
    boolean isDeleted;
    private Date createAt;
    private Date updateAt;
}
