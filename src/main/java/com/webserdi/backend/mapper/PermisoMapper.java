package com.webserdi.backend.mapper;

import com.webserdi.backend.dto.PermisoDto;
import com.webserdi.backend.entity.Permiso;
import org.springframework.stereotype.Component;

@Component
public class PermisoMapper {
    public static PermisoDto toDto(Permiso permiso) {
        PermisoDto dto = new PermisoDto();
        dto.setId(permiso.getId());
        dto.setNombre(permiso.getNombre());
        dto.setModuloId(permiso.getModulo().getId());
        return dto;
    }

    public static Permiso toEntity(PermisoDto dto) {
        Permiso permiso = new Permiso();
        permiso.setId(dto.getId());
        permiso.setNombre(dto.getNombre());
        // El módulo se asigna en el servicio
        return permiso;
    }
}
