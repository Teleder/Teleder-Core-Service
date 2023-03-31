package teleder.core.dtos;

import lombok.Data;

@Data
public class UserOnlineOfflinePayload {
    String id;
    Boolean active;

    public UserOnlineOfflinePayload(String id, boolean active) {
        this.id = id;
        this.active = active;
    }
}
