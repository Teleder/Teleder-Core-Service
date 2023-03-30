package teleder.core.services.File;

import org.springframework.web.multipart.MultipartFile;
import teleder.core.models.File.File;
import teleder.core.models.Message.Message;
import teleder.core.services.File.dtos.CreateFileDto;
import teleder.core.services.File.dtos.FileDto;
import teleder.core.services.File.dtos.UpdateFileDto;
import teleder.core.services.IMongoService;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface IFileService extends IMongoService<FileDto, CreateFileDto, UpdateFileDto> {
    public CompletableFuture<File> uploadFileCloud(MultipartFile file, String code) throws IOException;

    public CompletableFuture<Void> deleteFileCloud(String publicId);

    public CompletableFuture<File> uploadFileLocal(MultipartFile file, String code) throws IOException;

    public CompletableFuture<Void> deleteFileLocal(String fileName);

    CompletableFuture<List<File>> findFileWithPaginationAndSearch(long skip, int limit, String code);

    CompletableFuture<Long> countFileByCode(String code);
}
