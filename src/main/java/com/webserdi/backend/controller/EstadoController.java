package com.webserdi.backend.controller;

import com.webserdi.backend.dto.EstadoDto;
import com.webserdi.backend.service.EstadoService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/estados")
@AllArgsConstructor
public class EstadoController {

    private final EstadoService estadoService;

    @PostMapping
    public ResponseEntity<EstadoDto> createEstado(@RequestBody EstadoDto estadoDto) {
        EstadoDto createdEstado = estadoService.createEstado(estadoDto);
        return ResponseEntity.ok(createdEstado);
    }

    @GetMapping("/{estadoId}")
    public ResponseEntity<EstadoDto> getEstadoById(@PathVariable Long estadoId) {
        EstadoDto estadoDto = estadoService.getEstadoById(estadoId);
        return ResponseEntity.ok(estadoDto);
    }

    @GetMapping
    public ResponseEntity<List<EstadoDto>> getAllEstados() {
        List<EstadoDto> estados = estadoService.getAllEstados();
        return ResponseEntity.ok(estados);
    }

    @PutMapping("/{estadoId}")
    public ResponseEntity<EstadoDto> updateEstado(@PathVariable Long estadoId, @RequestBody EstadoDto estadoDto) {
        EstadoDto updatedEstado = estadoService.updateEstado(estadoId, estadoDto);
        return ResponseEntity.ok(updatedEstado);
    }

    @DeleteMapping("/{estadoId}")
    public ResponseEntity<Void> deleteEstado(@PathVariable Long estadoId) {
        estadoService.deleteEstado(estadoId);
        return ResponseEntity.noContent().build();
    }
}