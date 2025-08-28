package com.webserdi.backend.controller;

import com.webserdi.backend.dto.CarpetaDto;
import com.webserdi.backend.service.CarpetaService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/carpetas")
@AllArgsConstructor
public class CarpetaController {

    private final CarpetaService carpetaService;

    @PostMapping
    public ResponseEntity<CarpetaDto> createCarpeta(
            @RequestBody CarpetaDto carpetaDto,
            @RequestParam("usuarioId") Long usuarioId) {
        CarpetaDto nueva = carpetaService.createCarpeta(carpetaDto, usuarioId);
        return ResponseEntity.status(HttpStatus.CREATED).body(nueva);
    }

    @GetMapping("/{carpetaId}")
    public ResponseEntity<CarpetaDto> getCarpetaById(@PathVariable Long carpetaId) {
        CarpetaDto carpetaDto = carpetaService.getCarpetaById(carpetaId);
        return ResponseEntity.ok(carpetaDto);
    }

    @GetMapping
    public ResponseEntity<List<CarpetaDto>> getAllCarpetas() {
        List<CarpetaDto> carpetas = carpetaService.getAllCarpetas();
        return ResponseEntity.ok(carpetas);
    }

    @PutMapping("/{carpetaId}")
    public ResponseEntity<CarpetaDto> updateCarpeta(@PathVariable Long carpetaId, @RequestBody CarpetaDto carpetaDto) {
        CarpetaDto updatedCarpeta = carpetaService.updateCarpeta(carpetaId, carpetaDto);
        return ResponseEntity.ok(updatedCarpeta);
    }

    @DeleteMapping("/{carpetaId}")
    public ResponseEntity<Void> deleteCarpeta(@PathVariable Long carpetaId) {
        carpetaService.deleteCarpeta(carpetaId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<CarpetaDto>> getCarpetasByUsuario(@PathVariable Long usuarioId) {
        List<CarpetaDto> carpetas = carpetaService.getCarpetasByUsuario(usuarioId);
        return ResponseEntity.ok(carpetas);
    }

    @GetMapping("/eliminadas")
    public ResponseEntity<List<CarpetaDto>> listarCarpetasEliminadas(@RequestParam Long usuarioId) {
        return ResponseEntity.ok(carpetaService.getCarpetasEliminadasPorUsuario(usuarioId));
    }

    @PutMapping("/restaurar/{id}")
    public ResponseEntity<CarpetaDto> restaurarCarpeta(@PathVariable Long id) {
        return ResponseEntity.ok(carpetaService.restaurarCarpeta(id));
    }


}

