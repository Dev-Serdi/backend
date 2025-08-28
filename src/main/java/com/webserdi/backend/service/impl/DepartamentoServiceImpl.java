package com.webserdi.backend.service.impl;

import com.webserdi.backend.dto.DepartamentoDto;
import com.webserdi.backend.entity.Departamento;
import com.webserdi.backend.exception.ResourceNotFoundException;
import com.webserdi.backend.mapper.DepartamentoMapper;
import com.webserdi.backend.repository.DepartamentoRepository;
import com.webserdi.backend.service.DepartamentoService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
public class DepartamentoServiceImpl implements DepartamentoService {

    private final DepartamentoRepository departamentoRepository;
    private final DepartamentoMapper departamentoMapper;

    @Override
    public DepartamentoDto createDepartamento(DepartamentoDto departamentoDto) {
        Departamento departamento = departamentoMapper.toEntity(departamentoDto);
        departamento = departamentoRepository.save(departamento);
        return departamentoMapper.toDto(departamento);
    }

    @Override
    public DepartamentoDto getDepartamentoById(Long departamentoId) {
        Departamento departamento = departamentoRepository.findById(departamentoId)
                .orElseThrow(() -> new ResourceNotFoundException("Departamento not found with id: " + departamentoId));
        return departamentoMapper.toDto(departamento);
    }

    @Override
    public List<DepartamentoDto> getAllDepartamentos() {
        return departamentoRepository.findAll().stream()
                .map(departamentoMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public DepartamentoDto updateDepartamento(Long departamentoId, DepartamentoDto departamentoDto) {
        Departamento departamento = departamentoRepository.findById(departamentoId)
                .orElseThrow(() -> new ResourceNotFoundException("Departamento not found with id: " + departamentoId));
        departamento.setNombre(departamentoDto.getNombre());
        departamento = departamentoRepository.save(departamento);
        return departamentoMapper.toDto(departamento);
    }

    @Override
    public void deleteDepartamento(Long departamentoId) {
        Departamento departamento = departamentoRepository.findById(departamentoId)
                .orElseThrow(() -> new ResourceNotFoundException("Departamento not found with id: " + departamentoId));
        departamentoRepository.delete(departamento);
    }
}