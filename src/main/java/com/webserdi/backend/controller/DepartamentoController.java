package com.webserdi.backend.controller;

import com.webserdi.backend.dto.DepartamentoDto;
import com.webserdi.backend.service.DepartamentoService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/departamentos")
@AllArgsConstructor
public class DepartamentoController {

    private final DepartamentoService departamentoService;

    @PostMapping
    public ResponseEntity<DepartamentoDto> createDepartamento(@RequestBody DepartamentoDto departamentoDto) {
        DepartamentoDto createdDepartamento = departamentoService.createDepartamento(departamentoDto);
        return ResponseEntity.ok(createdDepartamento);
    }

    @GetMapping("/{departamentoId}")
    public ResponseEntity<DepartamentoDto> getDepartamentoById(@PathVariable Long departamentoId) {
        DepartamentoDto departamentoDto = departamentoService.getDepartamentoById(departamentoId);
        return ResponseEntity.ok(departamentoDto);
    }

    @GetMapping
    public ResponseEntity<List<DepartamentoDto>> getAllDepartamentos() {
        List<DepartamentoDto> departamentos = departamentoService.getAllDepartamentos();
        return ResponseEntity.ok(departamentos);
    }

    @PutMapping("/{departamentoId}")
    public ResponseEntity<DepartamentoDto> updateDepartamento(@PathVariable Long departamentoId, @RequestBody DepartamentoDto departamentoDto) {
        DepartamentoDto updatedDepartamento = departamentoService.updateDepartamento(departamentoId, departamentoDto);
        return ResponseEntity.ok(updatedDepartamento);
    }

    @DeleteMapping("/{departamentoId}")
    public ResponseEntity<Void> deleteDepartamento(@PathVariable Long departamentoId) {
        departamentoService.deleteDepartamento(departamentoId);
        return ResponseEntity.noContent().build();
    }
}