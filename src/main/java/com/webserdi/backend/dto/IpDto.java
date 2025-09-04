package com.webserdi.backend.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class IpDto {
    private Long id;
    private String ip;
    private String usuarioEmail;
    private String nombreUsuario; // Opcional: para mostrar el nombre del usuario
    private Long usuarioId; // Opcional: para mostrar el ID del usuario
    private LocalDateTime fechaRegistro;
}