package teleder.core.utils;

import teleder.core.models.File.FileCategory;

import java.nio.file.Path;
import java.nio.file.Paths;

public class FileCategorize {

    public static FileCategory categorize(String fileName) {
        Path path = Paths.get(fileName);
        String fileExtension = getFileExtension(path);

        switch (fileExtension.toLowerCase()) {
            case "mp4":
            case "avi":
            case "mov":
                return FileCategory.VIDEO;

            case "jpg":
            case "jpeg":
            case "png":
            case "gif":
                return FileCategory.IMAGE;

            case "mp3":
            case "wav":
            case "flac":
                return FileCategory.MUSIC;

            case "zip":
            case "rar":
            case "tar":
                return FileCategory.ZIP;

            case "doc":
            case "docx":
            case "pdf":
                return FileCategory.DOCUMENT;

            default:
                return FileCategory.OTHER;
        }
    }

    private static String getFileExtension(Path path) {
        String fileName = path.getFileName().toString();
        int dotIndex = fileName.lastIndexOf(".");
        if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
            return fileName.substring(dotIndex + 1);
        }
        return "";
    }


}
