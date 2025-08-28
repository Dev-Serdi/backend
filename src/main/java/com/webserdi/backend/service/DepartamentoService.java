package com.webserdi.backend.service;

import com.webserdi.backend.dto.DepartamentoDto;

import java.util.List;

public interface DepartamentoService {
    DepartamentoDto createDepartamento(DepartamentoDto departamentoDto);
    DepartamentoDto getDepartamentoById(Long departamentoId);
    List<DepartamentoDto> getAllDepartamentos();
    DepartamentoDto updateDepartamento(Long departamentoId, DepartamentoDto departamentoDto);
    void deleteDepartamento(Long departamentoId);
}