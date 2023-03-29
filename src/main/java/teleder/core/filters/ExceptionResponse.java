package teleder.core.filters;

import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
public class ExceptionResponse {
    int statusCode;
    String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss yyyy-MM-dd"));
    String message;
    String path;
    String error;
}
