package com.webserdi.backend.dto;

import lombok.Data;
import java.util.List;
import java.util.Set;

@Data
public class SitioDto {
    private Long id;
    private String siteId;
    private String name;
    private String description;
    private String visibility;
    private String type;
    private Long creadorId;
    private Set<UsuarioDto> usuariosAsignados; // Opción 2: DTOs de usuarios
    private Boolean favorito;

}
