package teleder.core.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import teleder.core.config.ApiPrefixController;
import teleder.core.config.Authenticate;
import teleder.core.dtos.PagedResultDto;
import teleder.core.dtos.Pagination;
import teleder.core.models.File.File;
import teleder.core.services.File.IFileService;

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
    @GetMapping("/{code}")
    public PagedResultDto<File> findMessagesWithPaginationAndSearch(@RequestParam(name = "page", defaultValue = "0") int page,
                                                                    @RequestParam(name = "size", defaultValue = "10") int size,
                                                                    @RequestParam(name = "content") String content,
                                                                    @PathVariable String code) {
        CompletableFuture<Long> total = fileService.countFileByCode(code);
        CompletableFuture<List<File>> files = fileService.findFileWithPaginationAndSearch(page * size, (page + 1) * size, code);
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(total, files);
        try {
            allFutures.get();
            return PagedResultDto.create(Pagination.create(total.get(), page * size, (page + 1) * size), files.get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("Some thing went wrong!");
    }

}
