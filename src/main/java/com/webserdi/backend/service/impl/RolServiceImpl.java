package com.webserdi.backend.service.impl;

import com.webserdi.backend.dto.RolDto;
import com.webserdi.backend.entity.Rol;
import com.webserdi.backend.mapper.RolMapper;
import com.webserdi.backend.repository.RolRepository;
import com.webserdi.backend.service.RolService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class RolServiceImpl implements RolService {
    private RolRepository rolRepository;

    @Override
    public RolDto createRol(RolDto rolDto) {
        Rol rol = RolMapper.mapToRol(rolDto);
        rol = rolRepository.save(rol);

        return RolMapper.mapToRolDto(rol);
    }

    @Override
    public List<RolDto> getAllRoles() {
        List<Rol> rol = rolRepository.findAll();
        return rol.stream()
                .map(RolMapper::mapToRolDto)
                .collect(Collectors.toList());
    }
}
