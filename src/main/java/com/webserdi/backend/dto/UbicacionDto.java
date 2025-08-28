package com.webserdi.backend.dto;

import com.webserdi.backend.entity.Ubicacion;
import lombok.Data;

@Data
public class UbicacionDto {
    private Long id;
    private String nombre;

    public UbicacionDto getDto(Ubicacion ubicacionSaved) {
        UbicacionDto dto = new UbicacionDto();
        dto.setId(ubicacionSaved.getId());
        dto.setNombre(ubicacionSaved.getNombre());
        return dto;
    }
}
