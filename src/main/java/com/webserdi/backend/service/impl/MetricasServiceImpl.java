package com.webserdi.backend.service.impl;

import com.webserdi.backend.dto.PerformanceReportDto;
import com.webserdi.backend.repository.TicketRepository;
import com.webserdi.backend.repository.projection.PerformanceProjection;
import com.webserdi.backend.service.MetricasService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service // CORRECCIÓN: Un servicio debe ser anotado con @Service, no @RestController
@RequiredArgsConstructor
public class MetricasServiceImpl implements MetricasService {

    private final TicketRepository ticketRepository;
    public LocalDateTime getSevenDaysAgo(){
        return LocalDateTime.now().minusDays(7);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PerformanceReportDto> getDepartmentPerformanceReport() {
        List<PerformanceProjection> projections = ticketRepository.getDepartmentPerformanceMetrics(getSevenDaysAgo());
        return projections.stream()
                .map(this::mapProjectionToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PerformanceReportDto> getReporteUsuariosPorModulo(Long moduloId) {
        return ticketRepository.getReporteUsuariosPorModulo(moduloId, getSevenDaysAgo()).stream()
                .map(this::mapProjectionToDto).collect(Collectors.toList());
    }

    private PerformanceReportDto mapProjectionToDto(PerformanceProjection projection) {
        return new PerformanceReportDto(
                projection.getGroupName(),
                projection.getDepartmentName(),
                projection.getTotalTickets(),
                projection.getTicketsCerrados(),
                projection.getTicketsActivos(),
                projection.getAvgResolutionTimeHoras()
        );
    }
}
