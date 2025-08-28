package com.webserdi.backend.service;

import com.webserdi.backend.dto.ModuloDto;
import com.webserdi.backend.dto.PermisoDto;

import java.util.List;

public interface ModuloService {
    ModuloDto crearModulo(ModuloDto moduloDto);
    List<ModuloDto> obtenerTodosModulos();
    List<PermisoDto> obtenerPermisosPorModulo(Long moduloId);
}