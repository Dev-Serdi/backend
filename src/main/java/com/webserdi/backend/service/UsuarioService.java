package com.webserdi.backend.service;

import com.webserdi.backend.dto.PermisoDto;
import com.webserdi.backend.dto.UsuarioDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;

public interface UsuarioService {

    UsuarioDto createUsuario(UsuarioDto usuarioDto);
    UsuarioDto getUsuarioById(Long usuarioId);
    Page<UsuarioDto> getAllUsuarios(Pageable pageable,String searchTerm);
    List<PermisoDto> getAllPermisos(String email);
    UsuarioDto updateUsuario(Long usuarioId,UsuarioDto usuarioDto);
    void deleteUsuario(Long usuarioId);
    String getRole (String email);
    Long getIdByEmail(String email);
    Page<UsuarioDto> getUsuarioByDepartamento (Long id, Pageable pageable);
    UsuarioDto getUsuarioByEmail(String email);
}
