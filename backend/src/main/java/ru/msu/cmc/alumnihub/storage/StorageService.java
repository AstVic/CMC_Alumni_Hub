package ru.msu.cmc.alumnihub.storage;

import org.springframework.web.multipart.MultipartFile;

/**
 * Stores uploaded files (currently alumni photos) and returns a public URL path.
 */
public interface StorageService {

    /** Stores the file and returns a public URL path (e.g. {@code /uploads/xxx.jpg}). */
    String storeImage(MultipartFile file);
}
