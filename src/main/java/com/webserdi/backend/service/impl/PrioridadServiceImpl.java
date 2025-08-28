package com.webserdi.backend.service.impl;

import com.webserdi.backend.dto.PrioridadDto;
import com.webserdi.backend.entity.Prioridad;
import com.webserdi.backend.exception.ResourceNotFoundException;
import com.webserdi.backend.mapper.PrioridadMapper;
import com.webserdi.backend.repository.PrioridadRepository;
import com.webserdi.backend.service.PrioridadService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
public class PrioridadServiceImpl implements PrioridadService {

    private final PrioridadRepository prioridadRepository;
    private final PrioridadMapper prioridadMapper;

    @Override
    public PrioridadDto createPrioridad(PrioridadDto prioridadDto) {
        Prioridad prioridad = prioridadMapper.toEntity(prioridadDto);
        prioridad = prioridadRepository.save(prioridad);
        return prioridadMapper.toDto(prioridad);
    }

    @Override
    public PrioridadDto getPrioridadById(Long prioridadId) {
        Prioridad prioridad = prioridadRepository.findById(prioridadId)
                .orElseThrow(() -> new ResourceNotFoundException("Prioridad not found with id: " + prioridadId));
        return prioridadMapper.toDto(prioridad);
    }

    @Override
    public List<PrioridadDto> getAllPrioridades() {
        return prioridadRepository.findAll().stream()
                .map(prioridadMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public PrioridadDto updatePrioridad(Long prioridadId, PrioridadDto prioridadDto) {
        Prioridad prioridad = prioridadRepository.findById(prioridadId)
                .orElseThrow(() -> new ResourceNotFoundException("Prioridad not found with id: " + prioridadId));
        prioridad.setNombre(prioridadDto.getNombre());
        prioridad = prioridadRepository.save(prioridad);
        return prioridadMapper.toDto(prioridad);
    }

    @Override
    public void deletePrioridad(Long prioridadId) {
        Prioridad prioridad = prioridadRepository.findById(prioridadId)
                .orElseThrow(() -> new ResourceNotFoundException("Prioridad not found with id: " + prioridadId));
        prioridadRepository.delete(prioridad);
    }
}