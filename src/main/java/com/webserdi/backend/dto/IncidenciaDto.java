package com.webserdi.backend.dto;

import lombok.Data;

@Data
public class IncidenciaDto {
    private Long id;
    private String nombre;
    private DepartamentoDto departamento;
}
