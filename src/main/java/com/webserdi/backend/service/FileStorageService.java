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