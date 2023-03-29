package teleder.core.models.Message;

import lombok.Data;

import java.util.Date;

@Data
public class HistoryChange {
    private String oldContent;
    private Date changeAt = new Date();
}
