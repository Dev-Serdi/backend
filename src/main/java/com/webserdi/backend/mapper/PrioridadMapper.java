package com.webserdi.backend.mapper;

import com.webserdi.backend.dto.PrioridadDto;
import com.webserdi.backend.entity.Prioridad;
import org.springframework.stereotype.Component;

@Component
public class PrioridadMapper {

    public Prioridad toEntity(PrioridadDto prioridadDto) {
        Prioridad prioridad = new Prioridad();
        prioridad.setId(prioridadDto.getId());
        prioridad.setNombre(prioridadDto.getNombre());
        return prioridad;
    }

    public PrioridadDto toDto(Prioridad prioridad) {
        PrioridadDto prioridadDto = new PrioridadDto();
        prioridadDto.setId(prioridad.getId());
        prioridadDto.setNombre(prioridad.getNombre());
        return prioridadDto;
    }
}