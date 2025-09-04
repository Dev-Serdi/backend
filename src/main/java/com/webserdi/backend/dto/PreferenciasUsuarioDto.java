// PreferenciasUsuarioDto.java - El objeto completo que se envía al frontend
package com.webserdi.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
public class PreferenciasUsuarioDto {
    private List<PreferenciaDto> todasLasPreferencias;
    private Set<String> preferenciasActivas; // Usamos Set<String> para que sea fácil de consumir en JSON
}