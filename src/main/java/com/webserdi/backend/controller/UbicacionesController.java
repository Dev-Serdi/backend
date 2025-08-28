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

    @PostMapping
    public ResponseEntity<UbicacionDto> createUbicacion(@RequestBody UbicacionDto dto){
        UbicacionDto ubicacionDto = ubicacionService.createUbicacion(dto);
        return ResponseEntity.ok(ubicacionDto);
    }
}
