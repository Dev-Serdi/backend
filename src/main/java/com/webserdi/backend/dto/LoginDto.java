package com.webserdi.backend.dto;

import lombok.Data;

@Data
public class LoginDto {
    private String email;
    private String password;
}