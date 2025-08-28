package com.webserdi.backend.service;

import
com.webserdi.backend.dto.PermisoDto;
import java.util.List;

public interface PermisoService {
    PermisoDto createPermiso(PermisoDto permisoDto);
    List<PermisoDto> getAllPermisos();

    PermisoDto getPermisoById(Long id);
}
