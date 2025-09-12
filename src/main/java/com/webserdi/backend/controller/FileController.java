package com.webserdi.backend.controller;

import com.webserdi.backend.service.FileStorageService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/files") // Separate endpoint for files
@RequiredArgsConstructor
public class FileController {

    private static final Logger logger = LoggerFactory.getLogger(FileController.class);
    private final FileStorageService fileStorageService;

    @GetMapping("/{filename:.+}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Resource> downloadFile(@PathVariable String filename, HttpServletRequest request) {
        // Cargar archivo como Resource desde Azure Blob Storage
        Resource resource = fileStorageService.loadFileAsResource(filename);

        // Intentar determinar el tipo de contenido
        String contentType = null;
        try {
            // Si es InputStreamResource (blob), no se puede usar getFile(), así que usar el nombre
            contentType = request.getServletContext().getMimeType(filename);
        } catch (Exception ex) {
            logger.info("No se pudo determinar el tipo de archivo para: {}", filename);
        }
        if (contentType == null) {
            contentType = "application/octet-stream";
        }
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                .body(resource);
    }
}