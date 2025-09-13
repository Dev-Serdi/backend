package com.webserdi.backend.controller;

import com.webserdi.backend.dto.PreferenciasUsuarioDto;
import com.webserdi.backend.service.PreferenciaUsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;
import java.util.Set;

@RestController
@RequestMapping("/api/usuarios/preferencias")
@RequiredArgsConstructor
public class PreferenciaUsuarioController {
    private final PreferenciaUsuarioService preferenciaUsuarioService;


    @GetMapping("/me")
    @Transactional(readOnly = true)
    public ResponseEntity<PreferenciasUsuarioDto> getMisPreferencias(@AuthenticationPrincipal UserDetails userDetails) {
        String correo = userDetails.getUsername();
        PreferenciasUsuarioDto preferenciasUsuarioDto = preferenciaUsuarioService.getPreferencias(correo);
        return ResponseEntity.ok(preferenciasUsuarioDto);
    }

    //convertir esta logica a servicio para aumentar seguridad.
    @PutMapping("/me")
    @Transactional
    public ResponseEntity<Void> updateMisPreferencias(@AuthenticationPrincipal UserDetails  userDetails, @RequestBody Set<String> nuevasPreferenciasActivas) {
        String correo = userDetails.getUsername();
        preferenciaUsuarioService.updatePreferencias(correo,nuevasPreferenciasActivas);
        return ResponseEntity.ok().build();
    }
}