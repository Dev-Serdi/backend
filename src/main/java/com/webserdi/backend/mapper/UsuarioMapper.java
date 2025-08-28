package com.webserdi.backend.mapper;

import com.webserdi.backend.dto.DepartamentoDto;
import com.webserdi.backend.dto.ModuloDto;
import com.webserdi.backend.dto.UsuarioDto;
import com.webserdi.backend.dto.UsuarioSimpleDto; // Importar si se usa aquí
import com.webserdi.backend.entity.*;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils; // Para comprobaciones de colecciones

import java.util.Collections;
import java.util.stream.Collectors;

/**
 * Mapper para convertir entre entidades {@link Usuario} y sus DTOs
 * ({@link UsuarioDto}, {@link UsuarioSimpleDto}).
 */
@Component // Aunque los métodos son estáticos, @Component permite la inyección si se decide cambiar
public class UsuarioMapper {

    /**
     * Convierte una entidad {@link Usuario} a un {@link UsuarioDto} completo.
     *
     * @param usuario La entidad Usuario a convertir.
     * @return El UsuarioDto resultante, o null si la entidad de entrada es null.
     */
    public UsuarioDto mapToUsuarioDto(Usuario usuario) {
        if (usuario == null) {
            return null;
        }

        UsuarioDto usuarioDto = new UsuarioDto();
        usuarioDto.setId(usuario.getId());
        usuarioDto.setEmail(usuario.getEmail());
        usuarioDto.setEnabled(usuario.isEnabled());
        usuarioDto.setNombre(usuario.getNombre());
        usuarioDto.setApellido(usuario.getApellido());
        if (usuario.getUbicacion() != null) usuarioDto.setUbicacion(usuario.getUbicacion());
        else usuarioDto.setUbicacion(null);

        // Mapear el departamento asociado
        Departamento departamentoEntity = usuario.getDepartamento();
        if (departamentoEntity != null) {
            DepartamentoDto departamentoDto = new DepartamentoDto();
            departamentoDto.setId(departamentoEntity.getId());
            departamentoDto.setNombre(departamentoEntity.getNombre());
            usuarioDto.setDepartamento(departamentoDto);
        }

        Modulo moduloEntity = usuario.getModulo();
        if (moduloEntity != null) {
            ModuloDto moduloDto = ModuloMapper.toDto(moduloEntity);
            usuarioDto.setModulo(moduloDto);
        }

        // Mapear roles
        if (!CollectionUtils.isEmpty(usuario.getRoles())) {
            usuarioDto.setRoles(usuario.getRoles().stream()
                    .map(Rol::getNombre)
                    .collect(Collectors.toSet()));
        } else {
            usuarioDto.setRoles(Collections.emptySet());
        }

        // Mapear permisos
        if (!CollectionUtils.isEmpty(usuario.getPermisos())) {
            usuarioDto.setPermisos(usuario.getPermisos().stream()
                    .map(Permiso::getNombre)
                    .collect(Collectors.toSet()));
        } else {
            usuarioDto.setPermisos(Collections.emptySet());
        }

        return usuarioDto;
    }

    /**
     * Convierte un {@link UsuarioDto} a una entidad {@link Usuario}.
     * Campos como la contraseña (si se actualiza), roles, permisos y departamento
     * se manejan y establecen en la capa de servicio.
     *
     * @param usuarioDto El UsuarioDto a convertir.
     * @return La entidad Usuario resultante, o null si el DTO de entrada es null.
     */
    public Usuario mapToUsuario(UsuarioDto usuarioDto) {
        if (usuarioDto == null) {
            return null;
        }

        Usuario usuario = new Usuario();
        // El ID se establece si es una actualización, sino es null para creación
        usuario.setId(usuarioDto.getId());
        usuario.setEmail(usuarioDto.getEmail());
        usuario.setEnabled(usuarioDto.isEnabled());
        usuario.setNombre(usuarioDto.getNombre());
        usuario.setApellido(usuarioDto.getApellido());
        // La contraseña, roles, permisos y departamento se establecen en el servicio.
        return usuario;
    }

    /**
     * Convierte una entidad {@link Usuario} a un {@link UsuarioSimpleDto}.
     *
     * @param usuario La entidad Usuario a convertir.
     * @return El UsuarioSimpleDto resultante, o null si la entidad de entrada es null.
     */
    public UsuarioSimpleDto mapToUsuarioSimpleDto(Usuario usuario) {
        if (usuario == null) {
            return null;
        }
        // Combina nombre y apellido para el UsuarioSimpleDto
        String nombreCompleto = usuario.getNombre() + " " + usuario.getApellido();
        return new UsuarioSimpleDto(usuario.getId(), nombreCompleto.trim());
    }
}