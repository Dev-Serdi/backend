package com.webserdi.backend.mapper;

import com.webserdi.backend.dto.SitioDto;
import com.webserdi.backend.entity.Sitio;
import org.springframework.stereotype.Component;
import java.util.HashSet;
import java.util.stream.Collectors;

@Component
public class SitioMapper {

    private final UsuarioMapper usuarioMapper; // Necesitarás un UsuarioMapper

    public SitioMapper(UsuarioMapper usuarioMapper) {
        this.usuarioMapper = usuarioMapper;
    }

    public Sitio toEntity(SitioDto dto) {
        Sitio sitio = new Sitio();
        sitio.setId(dto.getId());
        sitio.setNombre(dto.getName());
        sitio.setDescripcion(dto.getDescription());
        sitio.setTipo(dto.getType());
        sitio.setVisibilidad(dto.getVisibility());
        sitio.setSlug(dto.getSiteId());
        sitio.setFavorito(dto.getFavorito() != null ? dto.getFavorito() : false);


//        if (dto.getUsuariosAsignados() != null && !dto.getUsuariosAsignados().isEmpty()) {
//            // Convertir UsuarioDto a Usuario (necesitas implementar UsuarioMapper)
//            Set<Usuario> usuarios = dto.getUsuariosAsignados().stream()
//                    .map(usuarioDto -> usuarioRepo.findById(usuarioDto.getId())
//                            .orElseThrow(() -> new RuntimeException("Usuario no encontrado")))
//                    .collect(Collectors.toSet());
//            sitio.setUsuarios(usuarios);
//        } else {
//            sitio.setUsuarios(new HashSet<>());
//        }

        sitio.setAdministradores(new HashSet<>());
        return sitio;
    }

    public SitioDto toDto(Sitio sitio) {
        if (sitio == null) {
            return null;
        }

        SitioDto dto = new SitioDto();
        dto.setId(sitio.getId());
        dto.setName(sitio.getNombre());
        dto.setDescription(sitio.getDescripcion());
        dto.setType(sitio.getTipo());
        dto.setVisibility(sitio.getVisibilidad());
        dto.setSiteId(sitio.getSlug());
        dto.setFavorito(sitio.getFavorito());

        if (sitio.getCreador() != null) {
            dto.setCreadorId(sitio.getCreador().getId());
        }

        if (sitio.getUsuarios() != null && !sitio.getUsuarios().isEmpty()) {
            // Convertir Usuario a UsuarioDto
            dto.setUsuariosAsignados(sitio.getUsuarios().stream()
                    .map(usuarioMapper::mapToUsuarioDto) // Usar UsuarioMapper
                    .collect(Collectors.toSet()));
        }

        return dto;
    }
}