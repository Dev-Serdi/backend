package com.webserdi.backend.config;

import com.webserdi.backend.service.FileStorageService;
import com.webserdi.backend.service.impl.AzureBlobStorageServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StorageConfig {
    @Bean
    public FileStorageService fileStorageService(
            @Value("${azure.storage.account-name}") String accountName,
            @Value("${azure.storage.account-key}") String accountKey,
            @Value("${azure.storage.blob-container}") String containerName,
            @Value("${azure.storage.endpoint}") String endpoint
    ) {
        return new AzureBlobStorageServiceImpl(accountName, accountKey, containerName, endpoint);
    }
}
