package com.webserdi.backend.dto;

import com.webserdi.backend.entity.Rol;
import lombok.Data;

import java.util.Set;

@Data
public class JwtAuthResponse {
    private String accessToken;
    private String tokenType = "Bearer";
}
