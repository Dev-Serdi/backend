package com.webserdi.backend.mapper;

import com.webserdi.backend.dto.FuenteDto;
import com.webserdi.backend.entity.Fuente;
import org.springframework.stereotype.Component;

@Component
public class FuenteMapper {

    public Fuente toEntity(FuenteDto fuenteDto) {
        Fuente fuente = new Fuente();
        fuente.setId(fuenteDto.getId());
        fuente.setNombre(fuenteDto.getNombre());
        return fuente;
    }

    public FuenteDto toDto(Fuente fuente) {
        FuenteDto fuenteDto = new FuenteDto();
        fuenteDto.setId(fuente.getId());
        fuenteDto.setNombre(fuente.getNombre());
        return fuenteDto;
    }
}