package com.webserdi.backend.controller;

import com.webserdi.backend.dto.IncidenciaDto;
import com.webserdi.backend.service.IncidenciaService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus; // Importar para ResponseEntity.status
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/incidencias")
@AllArgsConstructor
public class IncidenciaController {

    private final IncidenciaService incidenciaService;

    @PostMapping
    public ResponseEntity<IncidenciaDto> createIncidencia(@RequestBody IncidenciaDto incidenciaDto) {
        // La validación del DTO y la lógica de asociación se manejan en el servicio
        IncidenciaDto createdIncidencia = incidenciaService.createIncidencia(incidenciaDto);
        // Devolver 201 Created con el objeto creado
        return ResponseEntity.status(HttpStatus.CREATED).body(createdIncidencia);
    }

    @GetMapping("/{incidenciaId}")
    public ResponseEntity<IncidenciaDto> getIncidenciaById(@PathVariable Long incidenciaId) {
        IncidenciaDto incidenciaDto = incidenciaService.getIncidenciaById(incidenciaId);
        return ResponseEntity.ok(incidenciaDto);
    }

    @GetMapping
    public ResponseEntity<List<IncidenciaDto>> getAllIncidencias() {
        List<IncidenciaDto> incidencias = incidenciaService.getAllIncidencias();
        return ResponseEntity.ok(incidencias);
    }

    @PutMapping("/{incidenciaId}")
    public ResponseEntity<IncidenciaDto> updateIncidencia(@PathVariable Long incidenciaId, @RequestBody IncidenciaDto incidenciaDto) {
        IncidenciaDto updatedIncidencia = incidenciaService.updateIncidencia(incidenciaId, incidenciaDto);
        return ResponseEntity.ok(updatedIncidencia);
    }

    @DeleteMapping("/{incidenciaId}")
    public ResponseEntity<Void> deleteIncidencia(@PathVariable Long incidenciaId) {
        incidenciaService.deleteIncidencia(incidenciaId);
        return ResponseEntity.noContent().build();
    }
}