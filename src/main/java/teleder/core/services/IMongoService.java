package teleder.core.services;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface IMongoService<T, D, E> {
    CompletableFuture<T> create(D input);

    CompletableFuture<T> getOne(String id);

    CompletableFuture<List<T>> getAll();

    CompletableFuture<T> update(String id, E input);

    CompletableFuture<Void> delete(String id);
}
