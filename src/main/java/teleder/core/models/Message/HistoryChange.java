package teleder.core.models.Message;

import lombok.Data;

import java.util.Date;

@Data
public class HistoryChange {
    private String content;
    private Date changeAt = new Date();
    public HistoryChange (String content){
        this.content = content;
    }
}
