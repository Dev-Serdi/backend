package com.webserdi.backend.controller;

import com.webserdi.backend.dto.PerformanceReportDto;
import com.webserdi.backend.service.MetricasService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
public class MetricasController {

    private final MetricasService metricasService;

    @GetMapping("/departamentos")
    public ResponseEntity<List<PerformanceReportDto>> getReporteDepartamentos(){
        return ResponseEntity.ok(metricasService.getDepartmentPerformanceReport());
    }

    @GetMapping("/usuarios")
    public ResponseEntity<List<PerformanceReportDto>> getReporteUsuarios(){
        return ResponseEntity.ok(metricasService.getReporteUsuariosPorModulo(1L));
    }
}
