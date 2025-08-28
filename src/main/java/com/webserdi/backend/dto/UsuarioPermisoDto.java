package com.webserdi.backend.dto;

import lombok.*;

import java.util.List;
@Data
public class UsuarioPermisoDto {
    private Long id;
    private List<String> permisos;
}
