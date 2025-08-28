package com.webserdi.backend.mapper;

import com.webserdi.backend.dto.EstadoDto;
import com.webserdi.backend.entity.Estado;
import org.springframework.stereotype.Component;

@Component
public class EstadoMapper {
    public Estado toEntity(EstadoDto estadoDto) {
        Estado estado = new Estado();
        estado.setId(estadoDto.getId());
        estado.setNombre(estadoDto.getNombre());
        return estado;
    }

    public EstadoDto toDto(Estado estado) {
        EstadoDto estadoDto = new EstadoDto();
        estadoDto.setId(estado.getId());
        estadoDto.setNombre(estado.getNombre());
        return estadoDto;
    }

}
