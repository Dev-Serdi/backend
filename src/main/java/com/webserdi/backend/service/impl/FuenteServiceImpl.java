package com.webserdi.backend.service.impl;

import com.webserdi.backend.dto.FuenteDto;
import com.webserdi.backend.entity.Fuente;
import com.webserdi.backend.exception.ResourceNotFoundException;
import com.webserdi.backend.mapper.FuenteMapper;
import com.webserdi.backend.repository.FuenteRepository;
import com.webserdi.backend.service.FuenteService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
public class FuenteServiceImpl implements FuenteService {

    private final FuenteRepository fuenteRepository;
    private final FuenteMapper fuenteMapper;

    @Override
    public FuenteDto createFuente(FuenteDto fuenteDto) {
        Fuente fuente = fuenteMapper.toEntity(fuenteDto);
        fuente = fuenteRepository.save(fuente);
        return fuenteMapper.toDto(fuente);
    }

    @Override
    public FuenteDto getFuenteById(Long fuenteId) {
        Fuente fuente = fuenteRepository.findById(fuenteId)
                .orElseThrow(() -> new ResourceNotFoundException("Fuente not found with id: " + fuenteId));
        return fuenteMapper.toDto(fuente);
    }

    @Override
    public List<FuenteDto> getAllFuentes() {
        return fuenteRepository.findAll().stream()
                .map(fuenteMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public FuenteDto updateFuente(Long fuenteId, FuenteDto fuenteDto) {
        Fuente fuente = fuenteRepository.findById(fuenteId)
                .orElseThrow(() -> new ResourceNotFoundException("Fuente not found with id: " + fuenteId));
        fuente.setNombre(fuenteDto.getNombre());
        fuente = fuenteRepository.save(fuente);
        return fuenteMapper.toDto(fuente);
    }

    @Override
    public void deleteFuente(Long fuenteId) {
        Fuente fuente = fuenteRepository.findById(fuenteId)
                .orElseThrow(() -> new ResourceNotFoundException("Fuente not found with id: " + fuenteId));
        fuenteRepository.delete(fuente);
    }
}