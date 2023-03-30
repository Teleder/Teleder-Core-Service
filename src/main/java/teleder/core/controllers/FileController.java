package teleder.core.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import teleder.core.config.ApiPrefixController;
import teleder.core.config.Authenticate;
import teleder.core.dtos.PagedResultDto;
import teleder.core.dtos.Pagination;
import teleder.core.models.File.File;
import teleder.core.services.File.IFileService;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
@ApiPrefixController("files")
public class FileController {
    @Autowired
    IFileService fileService;

    @Async
    @Authenticate
    @PostMapping(value = "/cloud/upload", consumes = "multipart/form-data")
    public CompletableFuture<File> uploadFileCloud(@RequestPart("file") MultipartFile file, @RequestParam(name = "code") String code) throws IOException {
        return fileService.uploadFileCloud(file, code);
    }

    @Async
    @Authenticate
    @DeleteMapping("/cloud/{publicId}")
    public CompletableFuture<Void> deleteFileCloud(@PathVariable String fileName) {
        return fileService.deleteFileLocal(fileName);
    }

    @Async
    @Authenticate
    @PostMapping(value = "/local/upload", consumes = "multipart/form-data")
    public CompletableFuture<File> uploadFileLocal(@RequestPart("file") MultipartFile file, @RequestParam(name = "code") String code) throws IOException {
        return fileService.uploadFileLocal(file, code);
    }

    @Async
    @Authenticate
    @DeleteMapping("/local/{fileName}")
    public CompletableFuture<Void> deleteFileLocal(@PathVariable String fileName) {
        return fileService.deleteFileLocal(fileName);
    }

    @Async
    @Authenticate
    @GetMapping("/{code}")
    public PagedResultDto<File> findMessagesWithPaginationAndSearch(@RequestParam(name = "page", defaultValue = "0") int page,
                                                                    @RequestParam(name = "size", defaultValue = "10") int size,
                                                                    @RequestParam(name = "content") String content,
                                                                    @PathVariable String code) {
        CompletableFuture<Long> total = fileService.countFileByCode(code);
        CompletableFuture<List<File>> files = fileService.findFileWithPaginationAndSearch(page * size, size, code);
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(total, files);
        try {
            allFutures.get();
            return PagedResultDto.create(Pagination.create(total.get(), page * size, size), files.get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("Some thing went wrong!");
    }

}
