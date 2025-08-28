package com.webserdi.backend.mapper;

import com.webserdi.backend.dto.CarpetaDto;
import com.webserdi.backend.entity.Carpeta;
import org.springframework.stereotype.Component;

@Component
public class CarpetaMapper {
    public Carpeta toEntity(CarpetaDto carpetaDto) {
        Carpeta carpeta = new Carpeta();
        carpeta.setId(carpetaDto.getId());
        carpeta.setNombre(carpetaDto.getNombre());
        return carpeta;
    }

    public CarpetaDto toDto(Carpeta carpeta) {
        CarpetaDto carpetaDto = new CarpetaDto();
        carpetaDto.setId(carpeta.getId());
        carpetaDto.setNombre(carpeta.getNombre());
        carpetaDto.setFechaCreacion(carpeta.getFechaCreacion());
        if (carpeta.getCarpetaPadre() != null) {
            carpetaDto.setCarpetaPadreId(carpeta.getCarpetaPadre().getId());
        }
        return carpetaDto;

    }

}
