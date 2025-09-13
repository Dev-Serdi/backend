package com.webserdi.backend.service;

import com.webserdi.backend.dto.PreferenciasUsuarioDto;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Set;

public interface PreferenciaUsuarioService {
    void updatePreferencias(String correo, Set<String> nuevasPreferenciasActivas);
    PreferenciasUsuarioDto getPreferencias(String correo);
}
