package com.webserdi.backend.mapper;

import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import com.webserdi.backend.dto.RolDto;
import com.webserdi.backend.entity.Permiso;
import com.webserdi.backend.entity.Rol;

@Component
public class RolMapper {
    public static Rol mapToRol(RolDto rolDto) {
        Rol rol = new Rol();
        rol.setId(rolDto.getId());
        rol.setNombre(rolDto.getNombre());
        // Los permisos se manejan en el servicio específico
        return rol;

    }
    public static RolDto mapToRolDto(Rol rol) {
        RolDto rolDto = new RolDto();
        rolDto.setId(rol.getId());
        rolDto.setNombre(rol.getNombre());
        rolDto.setPermisos(rol.getPermisos().stream()
                .map(Permiso::getNombre)
                .collect(Collectors.toSet()));
        return rolDto;
    }
}
