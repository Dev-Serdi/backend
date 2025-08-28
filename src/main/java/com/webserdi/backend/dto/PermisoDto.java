package com.webserdi.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PermisoDto {
    private Long id;
    private String nombre;
    private Long moduloId;  // Nuevo campo

}
