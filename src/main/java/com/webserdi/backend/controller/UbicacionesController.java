package com.webserdi.backend.controller;

import com.webserdi.backend.dto.UbicacionDto;
import com.webserdi.backend.service.UbicacionService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ubicaciones")
@AllArgsConstructor
public class UbicacionesController {
    private final UbicacionService ubicacionService;

    @GetMapping
    public ResponseEntity<List<UbicacionDto>> getAllUbicaciones(){
        List<UbicacionDto> ubicaciones = ubicacionService.getUbicaciones();
        return ResponseEntity.ok(ubicaciones);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UbicacionDto> getUbicacionById(@PathVariable Long id) {
        UbicacionDto ubicacionDto = ubicacionService.getUbicacionById(id);
        if (ubicacionDto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ubicacionDto);
    }

    @PostMapping
    public ResponseEntity<UbicacionDto> createUbicacion(@RequestBody UbicacionDto dto){
        UbicacionDto ubicacionDto = ubicacionService.createUbicacion(dto);
        return ResponseEntity.ok(ubicacionDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UbicacionDto> updateUbicacion(@PathVariable Long id, @RequestBody UbicacionDto dto) {
        UbicacionDto updated = ubicacionService.updateUbicacion(id, dto);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }
}
