// PreferenciaDto.java - Representa una opción de notificación
package com.webserdi.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PreferenciaDto {
    private String tipo;
    private String titulo;
    private String descripcion;
    private boolean configurable;
}