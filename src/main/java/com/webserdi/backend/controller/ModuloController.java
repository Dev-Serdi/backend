package com.webserdi.backend.controller;

import com.webserdi.backend.dto.ModuloDto;
import com.webserdi.backend.dto.PermisoDto;
import com.webserdi.backend.service.ModuloService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/modulos")
@AllArgsConstructor
public class ModuloController {

    private final ModuloService moduloService;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<ModuloDto> crearModulo(@Validated @RequestBody ModuloDto moduloDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(moduloService.crearModulo(moduloDTO));
    }


    @GetMapping
    public ResponseEntity<List<ModuloDto>> obtenerTodosModulos() {
        return ResponseEntity.ok(moduloService.obtenerTodosModulos());
    }

    @GetMapping("/{moduloId}/permisos")
    public ResponseEntity<List<PermisoDto>> obtenerPermisosPorModulo(@PathVariable Long moduloId) {
        return ResponseEntity.ok(moduloService.obtenerPermisosPorModulo(moduloId));
    }
}