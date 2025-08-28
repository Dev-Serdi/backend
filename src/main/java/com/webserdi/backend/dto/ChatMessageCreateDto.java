package com.webserdi.backend.dto;


import lombok.Data;

@Data
public class ChatMessageCreateDto {
    // Content is optional if a file is provided
    private String content;

    // File will be handled separately as MultipartFile in the controller
}