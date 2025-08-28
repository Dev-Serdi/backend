package com.webserdi.backend.exception;

/**
 * Custom exception for file storage related errors.
 * Extends RuntimeException so it doesn't need to be explicitly caught everywhere,
 * but can be handled by global exception handlers.
 */
public class FileStorageException extends RuntimeException {

    public FileStorageException(String message) {
        super(message);
    }

    public FileStorageException(String message, Throwable cause) {
        super(message, cause);
    }
}