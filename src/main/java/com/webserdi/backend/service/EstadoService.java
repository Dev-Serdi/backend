package com.webserdi.backend.service;

import com.webserdi.backend.dto.EstadoDto;

import java.util.List;

public interface EstadoService {
    EstadoDto createEstado(EstadoDto estadoDto);
    EstadoDto getEstadoById(Long estadoId);
    List<EstadoDto> getAllEstados();
    EstadoDto updateEstado(Long estadoId, EstadoDto estadoDto);
    void deleteEstado(Long estadoId);
}