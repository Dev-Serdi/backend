package com.webserdi.backend.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface AzureBlobStorageService {
    String uploadFile(MultipartFile file, String containerName);
    Resource downloadFile(String filename, String containerName);
}