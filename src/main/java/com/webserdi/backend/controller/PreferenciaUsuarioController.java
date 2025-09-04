package com.webserdi.backend.controller;

import com.webserdi.backend.dto.PreferenciaDto;
import com.webserdi.backend.dto.PreferenciasUsuarioDto;
import com.webserdi.backend.entity.TipoNotificacion;
import com.webserdi.backend.entity.Usuario;
import com.webserdi.backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/usuarios/preferencias")
@RequiredArgsConstructor
public class PreferenciaUsuarioController {

    private final UsuarioRepository usuarioRepository;

    @GetMapping("/me")
    @Transactional(readOnly = true)
    public ResponseEntity<PreferenciasUsuarioDto> getMisPreferencias(@AuthenticationPrincipal UserDetails userDetails) {
        Usuario usuario = usuarioRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 1. Obtener todas las preferencias posibles desde el Enum
        List<PreferenciaDto> todas = Arrays.stream(TipoNotificacion.values())
                .map(tipo -> new PreferenciaDto(tipo.name(), tipo.getTitulo(), tipo.getDescripcion(), tipo.isConfigurable()))
                .collect(Collectors.toList());

        // 2. Obtener las preferencias activas del usuario
        Set<String> activas = usuario.getNotificacionesActivas().stream()
                .map(Enum::name)
                .collect(Collectors.toSet());

        return ResponseEntity.ok(new PreferenciasUsuarioDto(todas, activas));
    }

    //convertir esta logica a servicio para aumentar seguridad.
    @PutMapping("/me")
    @Transactional
    public ResponseEntity<Void> updateMisPreferencias(@AuthenticationPrincipal UserDetails  userDetails, @RequestBody Set<String> nuevasPreferenciasActivas) {
        Usuario usuario = usuarioRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Convertimos los strings recibidos a nuestro Enum
        Set<TipoNotificacion> preferenciasEnum = nuevasPreferenciasActivas.stream()
                .map(TipoNotificacion::valueOf) // Esto convierte el String al Enum
                .collect(Collectors.toSet());

        usuario.setNotificacionesActivas(preferenciasEnum);
        usuarioRepository.save(usuario);

        return ResponseEntity.ok().build();
    }
}