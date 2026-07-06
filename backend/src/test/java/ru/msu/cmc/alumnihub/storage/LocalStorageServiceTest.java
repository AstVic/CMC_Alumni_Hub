package ru.msu.cmc.alumnihub.storage;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;
import ru.msu.cmc.alumnihub.common.exception.BadRequestException;
import ru.msu.cmc.alumnihub.config.properties.AppProperties;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LocalStorageServiceTest {

    private static final byte[] PNG = new byte[]{
            (byte) 0x89, 'P', 'N', 'G', 0x0D, 0x0A, 0x1A, 0x0A, 0, 0, 0, 0
    };

    @TempDir Path uploadDir;

    @Test
    void storesValidImageUnderGeneratedSafeName() throws Exception {
        LocalStorageService storage = storage();
        MockMultipartFile file = new MockMultipartFile(
                "file", "../../avatar.png", "image/png", PNG);

        String publicUrl = storage.storeImage(file);

        assertTrue(publicUrl.matches("/uploads/[0-9a-f-]+\\.png"));
        assertTrue(Files.exists(uploadDir.resolve(publicUrl.substring("/uploads/".length()))));
        assertEquals(1, Files.list(uploadDir).count());
    }

    @Test
    void rejectsSpoofedImageContent() {
        LocalStorageService storage = storage();
        MockMultipartFile file = new MockMultipartFile(
                "file", "avatar.png", "image/png", "not an image".getBytes());

        assertThrows(BadRequestException.class, () -> storage.storeImage(file));
    }

    @Test
    void rejectsMismatchedExtensionAndContentType() {
        LocalStorageService storage = storage();
        MockMultipartFile file = new MockMultipartFile(
                "file", "avatar.jpg", "image/png", PNG);

        assertThrows(BadRequestException.class, () -> storage.storeImage(file));
    }

    @Test
    void safelyDeletesOnlyStoredUploadPath() throws Exception {
        LocalStorageService storage = storage();
        String publicUrl = storage.storeImage(new MockMultipartFile(
                "file", "avatar.png", "image/png", PNG));
        Path stored = uploadDir.resolve(publicUrl.substring("/uploads/".length()));

        storage.delete("/uploads/../../outside.png");
        assertTrue(Files.exists(stored));

        storage.delete(publicUrl);
        assertFalse(Files.exists(stored));
    }

    private LocalStorageService storage() {
        AppProperties properties = new AppProperties(
                "http://localhost:5173",
                false,
                null,
                null,
                null,
                null,
                new AppProperties.Storage(uploadDir.toString()),
                null);
        return new LocalStorageService(properties);
    }
}
