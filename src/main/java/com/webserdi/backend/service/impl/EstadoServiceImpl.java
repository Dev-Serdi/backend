package com.webserdi.backend.service.impl;

import com.webserdi.backend.dto.EstadoDto;
import com.webserdi.backend.entity.Estado;
import com.webserdi.backend.exception.ResourceNotFoundException;
import com.webserdi.backend.mapper.EstadoMapper;
import com.webserdi.backend.repository.EstadoRepository;
import com.webserdi.backend.service.EstadoService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
public class EstadoServiceImpl implements EstadoService {

    private final EstadoRepository estadoRepository;
    private final EstadoMapper estadoMapper;

    @Override
    public EstadoDto createEstado(EstadoDto estadoDto) {
        Estado estado = estadoMapper.toEntity(estadoDto);
        estado = estadoRepository.save(estado);
        return estadoMapper.toDto(estado);
    }

    @Override
    public EstadoDto getEstadoById(Long estadoId) {
        Estado estado = estadoRepository.findById(estadoId)
                .orElseThrow(() -> new ResourceNotFoundException("Estado not found with id: " + estadoId));
        return estadoMapper.toDto(estado);
    }

    @Override
    public List<EstadoDto> getAllEstados() {
        return estadoRepository.findAll().stream()
                .map(estadoMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public EstadoDto updateEstado(Long estadoId, EstadoDto estadoDto) {
        Estado estado = estadoRepository.findById(estadoId)
                .orElseThrow(() -> new ResourceNotFoundException("Estado not found with id: " + estadoId));
        estado.setNombre(estadoDto.getNombre());
        estado = estadoRepository.save(estado);
        return estadoMapper.toDto(estado);
    }

    @Override
    public void deleteEstado(Long estadoId) {
        Estado estado = estadoRepository.findById(estadoId)
                .orElseThrow(() -> new ResourceNotFoundException("Estado not found with id: " + estadoId));
        estadoRepository.delete(estado);
    }
}