package com.webserdi.backend.service;

import com.webserdi.backend.dto.PerformanceReportDto;

import java.util.List;

public interface MetricasService {
    /**
     * Obtiene un reporte de rendimiento con métricas de tickets agrupadas por departamento.
     * @return Una lista de DTOs, cada uno representando las métricas de un departamento.
     */
    List<PerformanceReportDto> getDepartmentPerformanceReport();

    /**
     * Obtiene un reporte de rendimiento para usuarios de un módulo específico.
     * @param moduloId El ID del módulo a filtrar.
     * @return Una lista de DTOs con las métricas para cada usuario de ese módulo.
     */
    List<PerformanceReportDto> getReporteUsuariosPorModulo(Long moduloId);
}