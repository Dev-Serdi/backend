package com.webserdi.backend.service;

import com.webserdi.backend.dto.PrioridadDto;

import java.util.List;

public interface PrioridadService {
    PrioridadDto createPrioridad(PrioridadDto prioridadDto);
    PrioridadDto getPrioridadById(Long prioridadId);
    List<PrioridadDto> getAllPrioridades();
    PrioridadDto updatePrioridad(Long prioridadId, PrioridadDto prioridadDto);
    void deletePrioridad(Long prioridadId);
}