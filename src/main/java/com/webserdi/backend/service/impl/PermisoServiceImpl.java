package com.webserdi.backend.service.impl;

import com.webserdi.backend.dto.PermisoDto;
import com.webserdi.backend.entity.Modulo;
import com.webserdi.backend.entity.Permiso;
import com.webserdi.backend.exception.ResourceNotFoundException;
import com.webserdi.backend.mapper.PermisoMapper;
import com.webserdi.backend.repository.ModuloRepository;
import com.webserdi.backend.repository.PermisoRepository;
import com.webserdi.backend.service.PermisoService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PermisoServiceImpl implements PermisoService {
    private final ModuloRepository moduloRepository;
    private PermisoRepository permisoRepository;

    @Override
    public PermisoDto createPermiso(PermisoDto permisoDto) {
        Permiso permiso = PermisoMapper.toEntity(permisoDto);
        // 1. Retrieve the Modulo from the repository using the provided moduloId
        Modulo modulo = moduloRepository.findById(permisoDto.getModuloId())
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el módulo con ID: " + permisoDto.getModuloId()));

        // 2. Set the Modulo in the Permiso entity
        permiso.setModulo(modulo);

        // 3. Now save the Permiso, which includes the Modulo relationship.
        Permiso savedPermiso = permisoRepository.save(permiso);
        return PermisoMapper.toDto(savedPermiso);
    }
    @Override
    public PermisoDto getPermisoById(Long permisoId) {
        Permiso permiso = permisoRepository.findById(permisoId)
                .orElseThrow(()->
                        new ResourceNotFoundException("No existe el permiso con el id " + permisoId));
        return PermisoMapper.toDto(permiso);
    }

    @Override
    public List<PermisoDto> getAllPermisos() {
        List<Permiso> permiso = permisoRepository.findAll();
        return permiso.stream()
                .map(PermisoMapper::toDto)
                .collect(Collectors.toList());
    }
}
