package com.webserdi.backend.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

public interface FileStorageService {
    /**
     * Stores a file.
     * @param file The file to store.
     * @return The unique filename (or path relative to upload dir) under which the file was stored.
     */
    String storeFile(MultipartFile file);

    /**
     * Stores a file with a custom name (for blob/carpeta simulation).
     * @param file The file to store.
     * @param customName The custom name to use (e.g. carpeta__uuid.ext)
     * @return The name under which the file was stored.
     */
    default String storeFile(MultipartFile file, String customName) {
        // Por defecto, ignora el nombre personalizado y usa el método estándar
        return storeFile(file);
    }

    /**
     * Loads a file as a Resource.
     * @param filename The unique filename.
     * @return The Resource object.
     */
    Resource loadFileAsResource(String filename);

    /**
     * Gets the full path for a filename.
     * @param filename The unique filename.
     * @return The Path object.
     */
    Path getFilePath(String filename);

    // Optional: Add deleteFile method if needed
    // void deleteFile(String filename);
}