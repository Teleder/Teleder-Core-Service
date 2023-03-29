package teleder.core.services;

import teleder.core.models.Message.Message;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface IMongoService<T, D, E> {
    public CompletableFuture<T> create(D input);
    public CompletableFuture<T> getOne(String id);
    public CompletableFuture<List<T>> getAll();
    public CompletableFuture<T> update(String id, E input);
    public void delete(String id);

}
