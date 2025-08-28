package com.webserdi.backend.mapper;

import com.webserdi.backend.dto.ModuloDto;
import com.webserdi.backend.entity.Modulo;
import org.springframework.stereotype.Component;

@Component
public class ModuloMapper {
    public static Modulo toEntity(ModuloDto dto) {
        Modulo modulo = new Modulo();
        modulo.setId(dto.getId());
        modulo.setNombre(dto.getNombre());
        return modulo;
    }

    public static ModuloDto toDto(Modulo modulo) {
        ModuloDto dto = new ModuloDto();
        dto.setId(modulo.getId());
        dto.setNombre(modulo.getNombre());
        return dto;
    }
}
