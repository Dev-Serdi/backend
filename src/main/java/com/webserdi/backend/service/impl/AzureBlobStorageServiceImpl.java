package com.webserdi.backend.service.impl;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobContainerClientBuilder;
import com.azure.storage.blob.models.BlobHttpHeaders;
import com.webserdi.backend.exception.FileStorageException;
import com.webserdi.backend.exception.ResourceNotFoundException;
import com.webserdi.backend.service.FileStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
public class AzureBlobStorageServiceImpl implements FileStorageService {

    private final BlobContainerClient blobContainerClient;

    public AzureBlobStorageServiceImpl(
            @Value("${azure.storage.account-name}") String accountName,
            @Value("${azure.storage.account-key}") String accountKey,
            @Value("${azure.storage.blob-container}") String containerName,
            @Value("${azure.storage.endpoint}") String endpoint
    ) {
        String connectionString = String.format(
                "DefaultEndpointsProtocol=https;AccountName=%s;AccountKey=%s;EndpointSuffix=core.windows.net",
                accountName, accountKey
        );
        this.blobContainerClient = new BlobContainerClientBuilder()
                .connectionString(connectionString)
                .containerName(containerName)
                .endpoint(endpoint)
                .buildClient();
        if (!blobContainerClient.exists()) {
            blobContainerClient.create();
        }
    }
    @Override
    public String uploadFile(MultipartFile file, String containerName) {
        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);
        BlobClient blobClient = containerClient.getBlobClient(file.getOriginalFilename());
        try {
            blobClient.upload(file.getInputStream(), file.getSize(), true);
            return file.getOriginalFilename();
        } catch (IOException e) {
            throw new RuntimeException("Error al subir archivo al blob", e);
        }
    }

    @Override
    public String storeFile(MultipartFile file) {
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        String fileExtension = "";
        int dotIndex = originalFilename.lastIndexOf('.');
        if (dotIndex > 0) {
            fileExtension = originalFilename.substring(dotIndex);
        }
        String uniqueFilename = UUID.randomUUID().toString() + fileExtension;
        try (InputStream inputStream = file.getInputStream()) {
            BlobClient blobClient = blobContainerClient.getBlobClient(uniqueFilename);
            blobClient.upload(inputStream, file.getSize(), true);
            blobClient.setHttpHeaders(new BlobHttpHeaders().setContentType(file.getContentType()));
            return uniqueFilename;
        } catch (IOException e) {
            throw new FileStorageException("No se pudo subir el archivo a Azure Blob Storage.", e);
        }
    }

z|    // Nuevo método para subir archivo con nombre personalizado (simulando carpeta)
    public String storeFile(MultipartFile file, String blobName) {
        try (InputStream inputStream = file.getInputStream()) {
            BlobClient blobClient = blobContainerClient.getBlobClient(blobName);
            blobClient.upload(inputStream, file.getSize(), true);
            blobClient.setHttpHeaders(new BlobHttpHeaders().setContentType(file.getContentType()));
            return blobName;
        } catch (IOException e) {
            throw new FileStorageException("No se pudo subir el archivo a Azure Blob Storage.", e);
        }
    }

    @Override
    public Resource loadFileAsResource(String filename) {
        BlobClient blobClient = blobContainerClient.getBlobClient(filename);
        if (!blobClient.exists()) {
            throw new ResourceNotFoundException("Archivo no encontrado en blob: " + filename);
        }
        InputStream inputStream = blobClient.openInputStream();
        return new InputStreamResource(inputStream);
    }

    @Override
    public java.nio.file.Path getFilePath(String filename) {
        // No aplica para blob, pero se puede devolver null o lanzar excepción si se usa
        throw new UnsupportedOperationException("getFilePath no es aplicable para Azure Blob Storage");
    }
    @Override
    public Resource downloadFile(String filename, String containerName) {
        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);
        BlobClient blobClient = containerClient.getBlobClient(filename);
        return new InputStreamResource(blobClient.openInputStream());
    }
}
