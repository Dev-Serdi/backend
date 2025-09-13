package com.webserdi.backend.service.impl;

import com.webserdi.backend.dto.PreferenciaDto;
import com.webserdi.backend.dto.PreferenciasUsuarioDto;
import com.webserdi.backend.entity.TipoNotificacion;
import com.webserdi.backend.entity.Usuario;
import com.webserdi.backend.repository.UsuarioRepository;
import com.webserdi.backend.service.PreferenciaUsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PreferenciaUsuarioServiceImpl implements PreferenciaUsuarioService {
    private final UsuarioRepository usuarioRepository;

    @Override
    public void updatePreferencias(String correo, Set<String> nuevasPreferenciasActivas) {
        Usuario usuario = usuarioRepository.findByEmail(correo)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Convertimos los strings recibidos a nuestro Enum
        Set<TipoNotificacion> preferenciasEnum = nuevasPreferenciasActivas.stream()
                .map(TipoNotificacion::valueOf) // Esto convierte el String al Enum
                .collect(Collectors.toSet());

        usuario.setNotificacionesActivas(preferenciasEnum);
        usuarioRepository.save(usuario);
    }

    @Override
    public PreferenciasUsuarioDto getPreferencias(String correo) {
        Usuario usuario = usuarioRepository.findByEmail(correo)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 1. Obtener todas las preferencias posibles desde el Enum
        List<PreferenciaDto> todas = Arrays.stream(TipoNotificacion.values())
                .map(tipo -> new PreferenciaDto(tipo.name(), tipo.getTitulo(), tipo.getDescripcion(), tipo.isConfigurable()))
                .collect(Collectors.toList());

        // 2. Obtener las preferencias activas del usuario
        Set<String> activas = usuario.getNotificacionesActivas().stream()
                .map(Enum::name)
                .collect(Collectors.toSet());
        return (new PreferenciasUsuarioDto(todas, activas));
    }
}
