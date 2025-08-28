package com.webserdi.backend.service.impl;

import com.webserdi.backend.dto.ModuloDto;
import com.webserdi.backend.dto.PermisoDto;
import com.webserdi.backend.entity.Modulo;
import com.webserdi.backend.mapper.ModuloMapper;
import com.webserdi.backend.mapper.PermisoMapper;
import com.webserdi.backend.repository.ModuloRepository;
import com.webserdi.backend.repository.PermisoRepository;
import com.webserdi.backend.service.ModuloService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ModuloServiceImpl implements ModuloService {
    private final ModuloRepository moduloRepository;
    private final PermisoRepository permisoRepository;

    @Override
    public ModuloDto crearModulo(ModuloDto moduloDTO) {
        Modulo modulo = ModuloMapper.toEntity(moduloDTO);
        modulo = moduloRepository.save(modulo);
        return ModuloMapper.toDto(modulo);
    }

    @Override
    public List<ModuloDto> obtenerTodosModulos() {
        return moduloRepository.findAll().stream()
                .map(ModuloMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<PermisoDto> obtenerPermisosPorModulo(Long moduloId) {
        return permisoRepository.findByModuloId(moduloId).stream()
                .map(PermisoMapper::toDto)
                .collect(Collectors.toList());
    }
}