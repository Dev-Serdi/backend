package com.webserdi.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para transportar datos agregados para reportes de rendimiento.
 * Puede ser usado para agrupar por departamento, agente, etc.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PerformanceReportDto {

    /** El nombre de la entidad que se está midiendo (ej. "Soporte Técnico", "Juan Pérez"). */
    private String groupName;
    /** El nombre del departamento asociado al grupo (ej. el departamento del usuario). */
    private String departmentName;
    /** El número total de tickets asociados a este grupo. */
    private Long totalTickets;
    /** El número de tickets que actualmente están en un estado final ("Cerrado"). */
    private Long ticketsCerrados;
    /** El número de tickets que están actualmente en un estado activo ("En-Proceso"). */
    private Long ticketsActivos;
    /** El tiempo promedio en horas desde la creación hasta el cierre de los tickets resueltos. Puede ser nulo si no hay tickets resueltos. */
    private Double avgResolutionTimeHoras;
}
