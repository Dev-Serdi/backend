package com.webserdi.backend.controller;

import com.webserdi.backend.dto.PrioridadDto;
import com.webserdi.backend.service.PrioridadService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/prioridades")
@AllArgsConstructor
public class PrioridadController {

    private final PrioridadService prioridadService;

    @PostMapping
    public ResponseEntity<PrioridadDto> createPrioridad(@RequestBody PrioridadDto prioridadDto) {
        PrioridadDto createdPrioridad = prioridadService.createPrioridad(prioridadDto);
        return ResponseEntity.ok(createdPrioridad);
    }

    @GetMapping("/{prioridadId}")
    public ResponseEntity<PrioridadDto> getPrioridadById(@PathVariable Long prioridadId) {
        PrioridadDto prioridadDto = prioridadService.getPrioridadById(prioridadId);
        return ResponseEntity.ok(prioridadDto);
    }

    @GetMapping
    public ResponseEntity<List<PrioridadDto>> getAllPrioridades() {
        List<PrioridadDto> prioridades = prioridadService.getAllPrioridades();
        return ResponseEntity.ok(prioridades);
    }

    @PutMapping("/{prioridadId}")
    public ResponseEntity<PrioridadDto> updatePrioridad(@PathVariable Long prioridadId, @RequestBody PrioridadDto prioridadDto) {
        PrioridadDto updatedPrioridad = prioridadService.updatePrioridad(prioridadId, prioridadDto);
        return ResponseEntity.ok(updatedPrioridad);
    }

    @DeleteMapping("/{prioridadId}")
    public ResponseEntity<Void> deletePrioridad(@PathVariable Long prioridadId) {
        prioridadService.deletePrioridad(prioridadId);
        return ResponseEntity.noContent().build();
    }
}