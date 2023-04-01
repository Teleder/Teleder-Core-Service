package teleder.core.dtos;

import lombok.Data;

@Data
public class SocketPayload<T> {
   T data;
   String TYPE;
    public SocketPayload(T data, String type) {
        this.data = data;
        this.TYPE = type;
    }
    public static <T> SocketPayload create(T data, String type) {
        return new SocketPayload(data, type);
    }
}
