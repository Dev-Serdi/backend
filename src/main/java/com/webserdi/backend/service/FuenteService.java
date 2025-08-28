package com.webserdi.backend.service;

import com.webserdi.backend.dto.FuenteDto;

import java.util.List;

public interface FuenteService {
    FuenteDto createFuente(FuenteDto fuenteDto);
    FuenteDto getFuenteById(Long fuenteId);
    List<FuenteDto> getAllFuentes();
    FuenteDto updateFuente(Long fuenteId, FuenteDto fuenteDto);
    void deleteFuente(Long fuenteId);
}