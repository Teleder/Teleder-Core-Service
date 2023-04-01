package teleder.core.services.File;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import teleder.core.exceptions.NotFoundException;
import teleder.core.models.File.File;
import teleder.core.models.User.User;
import teleder.core.repositories.IFileRepository;
import teleder.core.repositories.IUserRepository;
import teleder.core.services.File.dtos.CreateFileDto;
import teleder.core.services.File.dtos.FileDto;
import teleder.core.services.File.dtos.UpdateFileDto;
import teleder.core.utils.FileCategorize;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class FileService implements IFileService {
    final
    IUserRepository userRepository;
    final
    IFileRepository fileRepository;
    private final Cloudinary cloudinary;

    public FileService(IUserRepository userRepository, IFileRepository fileRepository, Cloudinary cloudinary) {
        this.userRepository = userRepository;
        this.fileRepository = fileRepository;
        this.cloudinary = cloudinary;
    }

    @Override
    @Async
    public CompletableFuture<FileDto> create(CreateFileDto input) {
        return null;
    }

    @Override
    @Async
    public CompletableFuture<FileDto> getOne(String id) {
        return null;
    }

    @Override
    @Async
    public CompletableFuture<List<FileDto>> getAll() {
        return null;
    }

    @Override
    @Async
    public CompletableFuture<FileDto> update(String id, UpdateFileDto input) {
        return null;
    }


    @Override
    @Async
    public CompletableFuture<Void> delete(String id) {
        return null;
    }


    @Override
    public CompletableFuture<File> uploadFileCloud(MultipartFile file, String code) throws IOException {
        boolean isImage = file.getContentType().startsWith("image/");
        if (isImage) {
            byte[] fileData = file.getBytes();
            BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(fileData));
            int newWidth = 1920;
            int newHeight = 1080;
            boolean isLargeImage = originalImage != null && originalImage.getWidth() > newWidth && originalImage.getHeight() > newHeight;
            if (isLargeImage) {
                BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, originalImage.getType());
                Graphics2D g2d = resizedImage.createGraphics();
                g2d.drawImage(originalImage, 0, 0, newWidth, newHeight, null);
                g2d.dispose();
                ByteArrayOutputStream newImageBytes = new ByteArrayOutputStream();
                ImageIO.write(resizedImage, "jpg", newImageBytes);
                fileData = newImageBytes.toByteArray();
            }
            Map<String, Object> uploadResult = cloudinary.uploader().upload(fileData, ObjectUtils.emptyMap());
            String fileUrl = (String) uploadResult.get("url");
            String[] name = fileUrl.split("/");
            return CompletableFuture.completedFuture(fileRepository.insert(new File(name[name.length-1].split(".")[0], FileCategorize.categorize(file.getName()), file.getSize(), fileUrl, code)));
        } else {
            Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            String fileUrl = (String) uploadResult.get("url");
            String[] name = fileUrl.split("/");
            return CompletableFuture.completedFuture(fileRepository.insert(new File(name[name.length-1].split(".")[0], FileCategorize.categorize(file.getName()), file.getSize(), fileUrl, code)));
        }
    }

    @Override
    public CompletableFuture<Void> deleteFileCloud(String publicId) {
        try {
            if (publicId == null || publicId.trim() == "")
                throw new NotFoundException("Not found publicId");
            cloudinary.api().deleteResources(Arrays.asList(publicId), ObjectUtils.emptyMap());
            return CompletableFuture.completedFuture(null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public CompletableFuture<File> uploadFileLocal(MultipartFile file, String code) throws IOException {
        Path uploadPath = Paths.get(System.getProperty("user.dir"), "uploads");
        String fileName = System.currentTimeMillis() + "-" + UUID.randomUUID() + "." + FilenameUtils.getExtension(file.getOriginalFilename());
        boolean isImage = file.getContentType().startsWith("image/");
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        Path path = Paths.get(uploadPath.toString(), fileName);
        if (isImage) {
            byte[] fileData = file.getBytes();
            BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(fileData));
            int newWidth = 1920;
            int newHeight = 1080;
            boolean isLargeImage = originalImage != null && originalImage.getWidth() > newWidth && originalImage.getHeight() > newHeight;
            if (isLargeImage) {
                BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, originalImage.getType());
                Graphics2D g2d = resizedImage.createGraphics();
                g2d.drawImage(originalImage, 0, 0, newWidth, newHeight, null);
                g2d.dispose();
                ByteArrayOutputStream newImageBytes = new ByteArrayOutputStream();
                ImageIO.write(resizedImage, "jpg", newImageBytes);
                fileData = newImageBytes.toByteArray();
            }
            Files.write(path, fileData);
            String url = (request.getRemoteAddr().equalsIgnoreCase("0:0:0:0:0:0:0:1") ? "localhost" : request.getRemoteAddr()) + ":" + request.getLocalPort() + "/uploads/" + fileName;
            return CompletableFuture.completedFuture(fileRepository.insert(new File(fileName, FileCategorize.categorize(file.getName()), file.getSize(), url, code)));
        } else {
            Files.write(path, file.getBytes());
            String url = (request.getRemoteAddr().equalsIgnoreCase("0:0:0:0:0:0:0:1") ? "localhost" : request.getRemoteAddr()) + ":" + request.getLocalPort() + "/uploads/" + fileName;
            return CompletableFuture.completedFuture(fileRepository.insert(new File(fileName, FileCategorize.categorize(file.getName()), file.getSize(), url, code)));
        }
    }

    @Override
    public CompletableFuture<Void> deleteFileLocal(String fileName) {
        try {
            if (fileName == null || fileName.trim() == "")
                throw new NotFoundException("Not found file name");
            java.io.File file = new java.io.File(System.getProperty("user.dir"), "uploads/" + fileName);
            if (file.delete()) {
                return CompletableFuture.completedFuture(null);
            } else {
                throw new RuntimeException("Some thing went wrong!");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Async
    public CompletableFuture<List<File>> findFileWithPaginationAndSearch(long skip, int limit, String code) {
        String userId = ((User) (((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getAttribute("user"))).getId();
        if (!userRepository.findById(userId).get().getConservations().stream().anyMatch(elem -> elem.getCode().equals(code)))
            throw new NotFoundException("Not Found Conservation!");
        List<File> files = fileRepository.findFileWithPaginationAndSearch(skip, limit, code);
        return CompletableFuture.completedFuture(files);
    }

    @Override
    public CompletableFuture<Long> countFileByCode(String code) {
        return CompletableFuture.supplyAsync(() -> fileRepository.countFileByCode(code).orElse(0L));
    }
}
