package com.webserdi.backend.service;

import com.webserdi.backend.dto.IncidenciaDto;

import java.util.List;

public interface IncidenciaService {
    IncidenciaDto createIncidencia(IncidenciaDto incidenciaDto);
    IncidenciaDto getIncidenciaById(Long incidenciaId);
    List<IncidenciaDto> getAllIncidencias();
    IncidenciaDto updateIncidencia(Long incidenciaId, IncidenciaDto incidenciaDto);
    void deleteIncidencia(Long incidenciaId);
}