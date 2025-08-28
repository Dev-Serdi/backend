package com.webserdi.backend.mapper;

import com.webserdi.backend.dto.DepartamentoDto;
import com.webserdi.backend.dto.IncidenciaDto;
import com.webserdi.backend.entity.Departamento;
import com.webserdi.backend.entity.Incidencia;
import org.springframework.stereotype.Component;

@Component
public class IncidenciaMapper {
    public Incidencia toEntity(IncidenciaDto incidenciaDto){
        Incidencia incidencia = new Incidencia();
        incidencia.setId(incidenciaDto.getId());
        incidencia.setNombre(incidenciaDto.getNombre());
        return incidencia;
    }
    public IncidenciaDto toDto(Incidencia incidencia){
        IncidenciaDto dto = new IncidenciaDto();
        dto.setId(incidencia.getId());
        dto.setNombre(incidencia.getNombre());
        // Mapear el departamento asociado
        Departamento departamentoEntity = incidencia.getDepartamento();
        if (departamentoEntity != null) {
            DepartamentoDto departamentoDto = new DepartamentoDto();
            departamentoDto.setId(departamentoEntity.getId());
            departamentoDto.setNombre(departamentoEntity.getNombre());
            dto.setDepartamento(departamentoDto); // <-- Establecer el DTO anidado
        }
        // Si departamentoEntity es null, dto.getDepartamento() permanecerá null
        return dto;
    }
}
