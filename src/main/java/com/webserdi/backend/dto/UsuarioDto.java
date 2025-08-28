package com.webserdi.backend.dto;

import lombok.Data; // @Getter, @Setter, @ToString, @EqualsAndHashCode, @RequiredArgsConstructor
import java.util.HashSet;
import java.util.Set;

/**
 * Data Transfer Object para representar un Usuario.
 * Utilizado para la creación, actualización y visualización de usuarios.
 * Incluye información del departamento, roles y permisos.
 */
@Data
public class UsuarioDto {
    private Long id;
    private String nombre;
    private String apellido;
    private String email;
    private String password;
    private String ubicacion;
    private boolean enabled;
    private DepartamentoDto departamento; // Departamento al que pertenece el usuario
    private ModuloDto modulo; // Departamento al que pertenece el usuario
    private Set<String> roles = new HashSet<>(); // Nombres de los roles asignados
    private Set<String> permisos = new HashSet<>(); // Nombres de los permisos asignados
}