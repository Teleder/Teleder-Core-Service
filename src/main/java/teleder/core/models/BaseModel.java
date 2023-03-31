package teleder.core.models;

import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.util.Date;

@Data
public class BaseModel {
    boolean isDeleted = false;
    @CreatedBy
    private Date createAt = new Date();
    @LastModifiedDate
    private Date updateAt = new Date();
}
