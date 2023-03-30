package teleder.core.dtos;

import lombok.Data;

@Data
public class ListContactDto {
    String displayName;
    long skip;
    int limit;
}
