package com.webserdi.backend.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CarpetaDto {
    private Long id;
    private String nombre;
    private LocalDateTime fechaCreacion;
    private Long carpetaPadreId;
}
