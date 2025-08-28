package com.webserdi.backend.controller;

import com.webserdi.backend.dto.FuenteDto;
import com.webserdi.backend.service.FuenteService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fuentes")
@AllArgsConstructor
public class FuenteController {

    private final FuenteService fuenteService;

    @PostMapping
    public ResponseEntity<FuenteDto> createFuente(@RequestBody FuenteDto fuenteDto) {
        FuenteDto createdFuente = fuenteService.createFuente(fuenteDto);
        return ResponseEntity.ok(createdFuente);
    }

    @GetMapping("/{fuenteId}")
    public ResponseEntity<FuenteDto> getFuenteById(@PathVariable Long fuenteId) {
        FuenteDto fuenteDto = fuenteService.getFuenteById(fuenteId);
        return ResponseEntity.ok(fuenteDto);
    }

    @GetMapping
    public ResponseEntity<List<FuenteDto>> getAllFuentes() {
        List<FuenteDto> fuentes = fuenteService.getAllFuentes();
        return ResponseEntity.ok(fuentes);
    }

    @PutMapping("/{fuenteId}")
    public ResponseEntity<FuenteDto> updateFuente(@PathVariable Long fuenteId, @RequestBody FuenteDto fuenteDto) {
        FuenteDto updatedFuente = fuenteService.updateFuente(fuenteId, fuenteDto);
        return ResponseEntity.ok(updatedFuente);
    }

    @DeleteMapping("/{fuenteId}")
    public ResponseEntity<Void> deleteFuente(@PathVariable Long fuenteId) {
        fuenteService.deleteFuente(fuenteId);
        return ResponseEntity.noContent().build();
    }
}