package com.webserdi.backend.controller;

import com.webserdi.backend.dto.SitioDto;
import com.webserdi.backend.dto.UsuarioDto;
import com.webserdi.backend.service.SitioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/sitios")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SitioController {

    private final SitioService sitioService;

    @GetMapping("/{sitioId}")
    public ResponseEntity<SitioDto> obtenerSitioPorId(@PathVariable Long sitioId){
        return ResponseEntity.ok(sitioService.obtenerSitioPorId(sitioId));
    }

    @PostMapping
    public ResponseEntity<SitioDto> crearSitio(@RequestBody SitioDto sitioDto) {
        return ResponseEntity.ok(sitioService.crearSitio(sitioDto));
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<SitioDto>> listarSitiosPorUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(sitioService.listarMisSitios(usuarioId));
    }

    @GetMapping("/visibles")
    public ResponseEntity<List<SitioDto>> listarSitiosVisibles() {
        return ResponseEntity.ok(sitioService.listarSitiosPublicosYModerados());
    }

    @GetMapping("/slug/{id}")
    public ResponseEntity<List<SitioDto>> obtenerPorSlug(@PathVariable Long id) {
        return ResponseEntity.ok(sitioService.obtenerPorSlug(id));
    }

    @GetMapping("/{id}/usuarios")
    public ResponseEntity<Set<UsuarioDto>> obtenerUsuariosAsignados(@PathVariable Long id) {
        return ResponseEntity.ok(sitioService.obtenerUsuariosAsignados(id));
    }
    @PostMapping("/{id}/usuarios")
    public ResponseEntity<SitioDto> agregarUsuarios(@PathVariable Long id, @RequestBody Set<Long> nuevosIds) {
        return ResponseEntity.ok(sitioService.agregarUsuarios(id, nuevosIds));
    }

    @GetMapping("/eliminados")
    public ResponseEntity<List<SitioDto>> listarEliminados() {
        return ResponseEntity.ok(sitioService.listarSitiosEliminados());
    }

    @PutMapping("/restaurar/{id}")
    public ResponseEntity<SitioDto> restaurarSitio(@PathVariable Long id) {
        return ResponseEntity.ok(sitioService.restaurarSitio(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarSitio(@PathVariable Long id) {
        sitioService.eliminarSitio(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<SitioDto> actualizarSitio(@PathVariable Long id, @RequestBody SitioDto sitioDto) {
        return ResponseEntity.ok(sitioService.actualizarSitio(id, sitioDto));
    }
}
