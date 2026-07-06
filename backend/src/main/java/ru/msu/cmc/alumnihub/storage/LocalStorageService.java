package ru.msu.cmc.alumnihub.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.msu.cmc.alumnihub.common.exception.BadRequestException;
import ru.msu.cmc.alumnihub.config.properties.AppProperties;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

/**
 * Stores images on the local filesystem (a mounted volume in Docker). The public
 * URL is served statically at {@code /uploads/**}.
 */
@Service
public class LocalStorageService implements StorageService {

    private static final Logger log = LoggerFactory.getLogger(LocalStorageService.class);
    private static final long MAX_SIZE = 5 * 1024 * 1024; // 5 MB
    private static final Set<String> ALLOWED = Set.of("image/jpeg", "image/png", "image/webp");

    private final Path uploadDir;

    public LocalStorageService(AppProperties appProperties) {
        this.uploadDir = Paths.get(appProperties.storage().uploadDir()).toAbsolutePath().normalize();
        try {
            Files.createDirectories(uploadDir);
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot create upload directory " + uploadDir, e);
        }
    }

    @Override
    public String storeImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Файл не выбран");
        }
        if (file.getSize() > MAX_SIZE) {
            throw new BadRequestException("Файл слишком большой (максимум 5 МБ)");
        }
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED.contains(contentType)) {
            throw new BadRequestException("Разрешены только изображения JPEG, PNG или WebP");
        }

        String extension = switch (contentType) {
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            default -> ".jpg";
        };
        String filename = UUID.randomUUID() + extension;
        Path target = uploadDir.resolve(filename).normalize();
        if (!target.startsWith(uploadDir)) {
            throw new BadRequestException("Некорректное имя файла");
        }

        try {
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to store file", e);
        }
        log.info("Stored image {}", filename);
        return "/uploads/" + filename;
    }
}
