package teleder.core.services;

import com.google.zxing.WriterException;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public interface IMongoService<T, D, E> {
    CompletableFuture<T> create(D input) throws WriterException, IOException, ExecutionException, InterruptedException;

    CompletableFuture<T> getOne(String id);

    CompletableFuture<List<T>> getAll();

    CompletableFuture<T> update(String id, E input);

    CompletableFuture<Void> delete(String id);
}
