package com.webserdi.backend.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PublicacionDto {
    private Long id;
    private Long sitioId;
    private Long autorId;
    private String contenido;
    private String nombreArchivo;
    private String tipoArchivo;
    private LocalDateTime fechaPublicacion;
}

