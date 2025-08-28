package com.webserdi.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioSimpleDto {
    private Long id;
    private String nombre; // Combined first and last name
}