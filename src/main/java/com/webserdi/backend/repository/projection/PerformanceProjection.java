package com.webserdi.backend.repository.projection;

/**
 * Interfaz de proyección para mapear los resultados de consultas de métricas agregadas.
 * Spring Data JPA creará implementaciones proxy de esta interfaz automáticamente.
 */
public interface PerformanceProjection {
    String getGroupName();
    String getDepartmentName(); // Nuevo campo para el nombre del departamento
    Long getTotalTickets();
    Long getTicketsCerrados();
    Long getTicketsActivos();
    Double getAvgResolutionTimeHoras();
}