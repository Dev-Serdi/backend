package com.webserdi.backend.mapper;

import com.webserdi.backend.dto.DepartamentoDto;
import com.webserdi.backend.entity.Departamento;
import org.springframework.stereotype.Component;

@Component
public class DepartamentoMapper {

    public Departamento toEntity(DepartamentoDto departamentoDto) {
        Departamento departamento = new Departamento();
        departamento.setId(departamentoDto.getId());
        departamento.setNombre(departamentoDto.getNombre());
        return departamento;
    }

    public DepartamentoDto toDto(Departamento departamento) {
        DepartamentoDto departamentoDto = new DepartamentoDto();
        departamentoDto.setId(departamento.getId());
        departamentoDto.setNombre(departamento.getNombre());
        return departamentoDto;
    }
}