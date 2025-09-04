package com.webserdi.backend.controller;

import com.webserdi.backend.dto.PermisoDto;
import com.webserdi.backend.dto.UsuarioDto;
import com.webserdi.backend.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // Asegúrate que las anotaciones de seguridad sean correctas
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Set;
/**
 * Controlador REST para la gestión de Usuarios.
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    /**
     * Crea un nuevo usuario.
     * Se requiere rol de administrador (ejemplo, ajustar según necesidad).
     *
     * @param usuarioDto DTO con la información del usuario a crear.
     * @return DTO del usuario creado con estado HTTP 201 (Created).
     */
    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')") // Ejemplo de autorización
    public ResponseEntity<UsuarioDto> createUsuario(@RequestBody UsuarioDto usuarioDto) {
        UsuarioDto createdUsuario = usuarioService.createUsuario(usuarioDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUsuario);
    }

    /**
     * Obtiene un usuario por su ID.
     *
     * @param usuarioId ID del usuario.
     * @return DTO del usuario encontrado.
     */
    @GetMapping("/{id}") // Consistencia en el path variable
    public ResponseEntity<UsuarioDto> getUsuarioById(@PathVariable("id") Long usuarioId) {
        UsuarioDto usuarioDto = usuarioService.getUsuarioById(usuarioId);
        return ResponseEntity.ok(usuarioDto);
    }

    /**
     * Obtiene una lista de todos los usuarios.
     * Se requiere rol de administrador (ejemplo).
     *
     * @return Lista de DTOs de usuarios.
     */
    // En UsuarioController.java
    @GetMapping
    public ResponseEntity<Page<UsuarioDto>> getAllUsuarios(
            @PageableDefault(size = 8, sort = "nombre") Pageable pageable,
            @RequestParam(required = false) String searchTerm) {
        Page<UsuarioDto> usuarios = usuarioService.getAllUsuarios(pageable,searchTerm);
        return ResponseEntity.ok(usuarios);
    }
    @GetMapping("/departamento/{id}")
    public ResponseEntity<Page<UsuarioDto>>getUsuariosByDepartamento(
            @PageableDefault(size = 8, sort = "nombre") Pageable pageable,
            @PathVariable("id") Long departamentoId){
        Page<UsuarioDto> usuarios = usuarioService.getUsuarioByDepartamento(departamentoId,pageable);
        return ResponseEntity.ok(usuarios);
    }
    /**
     * Actualiza un usuario existente.
     * Se requiere rol de administrador.
     *
     * @param usuarioId ID del usuario a actualizar.
     * @param usuarioDto DTO con la información actualizada.
     * @return DTO del usuario actualizado.
     */
    @PutMapping("/edit/{id}") // Path original mantenido
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<UsuarioDto> updateUsuario(@PathVariable("id") Long usuarioId, @RequestBody UsuarioDto usuarioDto) {
        UsuarioDto updatedUsuario = usuarioService.updateUsuario(usuarioId, usuarioDto);
        return ResponseEntity.ok(updatedUsuario);
    }

    /**
     * Elimina un usuario.
     * Se requiere rol de administrador.
     *
     * @param usuarioId ID del usuario a eliminar.
     * @return Respuesta HTTP 204 (No Content) si la eliminación es exitosa.
     */
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteUsuario(@PathVariable("id") Long usuarioId) {
        usuarioService.deleteUsuario(usuarioId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Obtiene todos los permisos disponibles en el sistema.
     *
     * @return Lista de DTOs de permisos.
     */
    @GetMapping("/permisos")
    public ResponseEntity<List<PermisoDto>> getAllPermisos(Authentication auth) {
        String email = auth.getName();
        List<PermisoDto> permisos = usuarioService.getAllPermisos(email);
        return ResponseEntity.ok(permisos);
    }

    /**
     * Obtiene los roles de un usuario basado en su email.
     *
     * @return Conjunto de nombres de roles.
     */
    @GetMapping("/email/roles") // Endpoint más descriptivo
    public ResponseEntity<Set<String>> getRolesByEmail(Authentication auth) {
        String email = auth.getName();
        Set<String> roles = usuarioService.getRole(email);
        return ResponseEntity.ok(roles);
    }

    /**
     * Obtiene el ID del usuario autenticado actualmente.
     *
     * @param auth Objeto de autenticación proporcionado por Spring Security.
     * @return ID del usuario autenticado como String.
     */
    @GetMapping("/me/id") // Endpoint más descriptivo para el usuario actual
    public ResponseEntity<String> getCurrentAuthenticatedUserId(Authentication auth) {
        if (!auth.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autenticado.");
        }
        Long id = usuarioService.getIdByEmail(auth.getName());
        return ResponseEntity.ok(id.toString());
    }

    @GetMapping("/me/email")
    public ResponseEntity<UsuarioDto>getCurrentUserByEmail(Authentication auth){
        if (!auth.isAuthenticated()) {
            return null;
        }
        UsuarioDto usuario = usuarioService.getUsuarioByEmail(auth.getName());
        return ResponseEntity.ok(usuario);
    }
}