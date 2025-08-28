package com.webserdi.backend.service;

import com.webserdi.backend.dto.SitioDto;
import com.webserdi.backend.dto.UsuarioDto;

import java.util.List;
import java.util.Set;

public interface SitioService {
    SitioDto crearSitio(SitioDto sitioDto);
    List<SitioDto> listarMisSitios(Long usuarioId);
    List<SitioDto> obtenerPorSlug(Long id);
    void eliminarSitio(Long id);
    SitioDto actualizarSitio(Long id, SitioDto sitioDto);
    List<SitioDto> listarSitiosPublicosYModerados();
    Set<UsuarioDto> obtenerUsuariosAsignados(Long sitioId);
    SitioDto agregarUsuarios(Long sitioId, Set<Long> usuariosNuevosIds);
    // SitioService.java
    List<SitioDto> listarSitiosEliminados();
    SitioDto restaurarSitio(Long id); // También asegúrate de que este esté declarado
    SitioDto obtenerSitioPorId(Long sitioId);
}
