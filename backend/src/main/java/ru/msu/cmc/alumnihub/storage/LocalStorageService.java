package ru.msu.cmc.alumnihub.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.msu.cmc.alumnihub.common.exception.BadRequestException;
import ru.msu.cmc.alumnihub.common.exception.StorageException;
import ru.msu.cmc.alumnihub.config.properties.AppProperties;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
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
    private static final Set<String> ALLOWED_TYPES =
            Set.of("image/jpeg", "image/png", "image/webp");
    private static final Set<String> ALLOWED_EXTENSIONS =
            Set.of("jpg", "jpeg", "png", "webp");

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
        if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
            throw new BadRequestException("Разрешены только изображения JPEG, PNG или WebP");
        }
        String originalExtension = extensionOf(file.getOriginalFilename());
        if (!ALLOWED_EXTENSIONS.contains(originalExtension)) {
            throw new BadRequestException("Недопустимое расширение файла");
        }
        validateTypeMatchesExtension(contentType, originalExtension);

        byte[] header = readHeader(file);
        if (!matchesSignature(contentType, header)) {
            throw new BadRequestException("Содержимое файла не соответствует формату изображения");
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
            throw new StorageException("Не удалось сохранить изображение", e);
        }
        log.info("Stored image {}", filename);
        return "/uploads/" + filename;
    }

    @Override
    public void delete(String publicUrl) {
        if (publicUrl == null || !publicUrl.startsWith("/uploads/")) {
            return;
        }
        String filename = publicUrl.substring("/uploads/".length());
        if (filename.isBlank() || filename.contains("/") || filename.contains("\\")) {
            log.warn("Refusing to delete unsafe upload path");
            return;
        }
        Path target = uploadDir.resolve(filename).normalize();
        if (!target.startsWith(uploadDir)) {
            log.warn("Refusing to delete upload outside storage directory");
            return;
        }
        try {
            if (Files.deleteIfExists(target)) {
                log.info("Deleted replaced image {}", filename);
            }
        } catch (IOException ex) {
            // A stale old photo must not make an otherwise valid profile update fail.
            log.warn("Could not delete replaced image {}: {}", filename, ex.getMessage());
        }
    }

    private byte[] readHeader(MultipartFile file) {
        try (InputStream input = file.getInputStream()) {
            return input.readNBytes(12);
        } catch (IOException ex) {
            throw new StorageException("Не удалось прочитать изображение", ex);
        }
    }

    private boolean matchesSignature(String contentType, byte[] header) {
        return switch (contentType) {
            case "image/jpeg" -> header.length >= 3
                    && unsigned(header[0]) == 0xFF
                    && unsigned(header[1]) == 0xD8
                    && unsigned(header[2]) == 0xFF;
            case "image/png" -> header.length >= 8
                    && unsigned(header[0]) == 0x89
                    && header[1] == 'P' && header[2] == 'N' && header[3] == 'G'
                    && unsigned(header[4]) == 0x0D && unsigned(header[5]) == 0x0A
                    && unsigned(header[6]) == 0x1A && unsigned(header[7]) == 0x0A;
            case "image/webp" -> header.length >= 12
                    && header[0] == 'R' && header[1] == 'I' && header[2] == 'F' && header[3] == 'F'
                    && header[8] == 'W' && header[9] == 'E' && header[10] == 'B' && header[11] == 'P';
            default -> false;
        };
    }

    private void validateTypeMatchesExtension(String contentType, String extension) {
        boolean matches = switch (contentType) {
            case "image/jpeg" -> extension.equals("jpg") || extension.equals("jpeg");
            case "image/png" -> extension.equals("png");
            case "image/webp" -> extension.equals("webp");
            default -> false;
        };
        if (!matches) {
            throw new BadRequestException("Расширение файла не соответствует Content-Type");
        }
    }

    private String extensionOf(String filename) {
        if (filename == null) {
            return "";
        }
        String safeName = filename.replace('\\', '/');
        safeName = safeName.substring(safeName.lastIndexOf('/') + 1);
        int dot = safeName.lastIndexOf('.');
        return dot < 0 ? "" : safeName.substring(dot + 1).toLowerCase(Locale.ROOT);
    }

    private int unsigned(byte value) {
        return value & 0xFF;
    }
}
