package com.webserdi.backend.repository;

import com.webserdi.backend.entity.Ticket;
import com.webserdi.backend.repository.projection.PerformanceProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long>, JpaSpecificationExecutor<Ticket> {
    @Query("SELECT MAX(t.codigo) FROM Ticket t")
    String findMaxCodigo();
    Page<Ticket> findAllByIsTrashedTrue(Pageable pageable);
    Page<Ticket> findAllByIsTrashedFalseOrderByFechaCreacionDesc(Pageable pageable);
//    Page<Ticket> findAllByDepartamentoNombreAndIsTrashedFalse(String departamento, Pageable pageable);
    Page<Ticket> findAllByDepartamentoNombreAndIsTrashedFalseOrderByFechaCreacionDesc(String departamento, Pageable pageable);
    Page<Ticket> findAllByEstadoNombreAndIsTrashedFalse(String filtro, Pageable pageable);
    Page<Ticket> findAllByEstadoNombreAndDepartamentoNombreAndIsTrashedFalse(String filtro, String departamento, Pageable pageable);
    Page<Ticket> findAllByUsuarioCreadorId(Pageable pageable, Long id);
    Page<Ticket> findAllByEstadoIdAndUsuarioCerrarIsNotNull(Pageable pageable, Long estadoId); //Retorna un page con los tickets que hayan sido re-abiertos
    Page<Ticket> findAllByUsuarioCreadorIdAndEstadoId(Pageable pageable, Long id, Long estadoId);
    //Devuelve tickets por tema o código. Su única función es una busqueda de tickets.
    Page<Ticket> findAllByTemaContainsIgnoreCaseAndIsTrashedFalseOrCodigoContainsIgnoreCaseAndIsTrashedFalse(String tema, String codigo, Pageable pageable);
    //Querys para los reportes
    Page<Ticket> findAllByUsuarioAsignadoIdAndEstadoId(Pageable pageable, Long id, Long estadoId);//Este query se usa tambien para mostrar los tickets del usuario que tiene pendientes por realizar
    Page<Ticket> findAllByUsuarioAsignadoId(Pageable pageable, Long id);//Este query se usa tambien para mostrar los tickets del usuario que tiene pendientes por realizar
    // Busca todos los tickets sin respuesta
    Page<Ticket> findAllByIsTrashedFalseAndFechaRespuestaIsNull(Pageable pageable);
    Page<Ticket> findAllByIsTrashedFalseAndUsuarioAsignadoIdAndFechaRespuestaIsNull(Long usuarioId, Pageable pageable);

    // Busca tickets sin respuesta dentro de un departamento específico
    Page<Ticket> findAllByIsTrashedFalseAndDepartamentoNombreAndFechaRespuestaIsNull(String departamento, Pageable pageable);
    /**
     *
     * Calcula métricas de rendimiento de tickets, agrupadas por nombre de departamento.
     * - Un ticket se considera "Cerrado" si tiene una fecha de cierre. De lo contrario, se considera "Activo".
     * - El tiempo de resolución se calcula en horas.
     * @return Una lista de proyecciones con las métricas para cada departamento.
     */
    @Query("SELECT " +
            "d.nombre AS groupName, " +
            "COALESCE(COUNT(t.id), 0L) AS totalTickets, " +
            "COALESCE(SUM(CASE WHEN t.fechaCierre IS NOT NULL THEN 1 ELSE 0 END), 0L) AS ticketsCerrados, " +
            "COALESCE(SUM(CASE WHEN t.fechaCierre IS NULL THEN 1 ELSE 0 END), 0L) AS ticketsActivos, " +
            // AVG ahora solo necesita verificar que el ticket esté cerrado, ya que el filtro de fecha está en el WHERE
            "AVG(CASE " +
            "    WHEN t.fechaCierre IS NOT NULL " +
            "    THEN CAST(TIMESTAMPDIFF(MINUTE, t.fechaCreacion, t.fechaCierre) AS double) / 60.0 " +
            "    ELSE NULL " +
            "END) AS avgResolutionTimeHoras " +
            "FROM Ticket t JOIN t.departamento d " +
            // AÑADIDO: Filtro para incluir solo tickets creados en los últimos 7 días
            "WHERE t.isTrashed = false AND t.fechaCreacion >= :sevenDaysAgo " +
            "GROUP BY d.nombre, d.id " +
            "ORDER BY d.nombre")
    List<PerformanceProjection> getDepartmentPerformanceMetrics(@Param("sevenDaysAgo") LocalDateTime sevenDaysAgo);
    /**
     * Calcula métricas de rendimiento para los tickets asignados a usuarios de un módulo específico.
     * Agrupa los resultados por usuario y su departamento.
     *
     * @param moduloId El ID del módulo por el cual filtrar los usuarios.
     * @return Una lista de proyecciones con las métricas para cada usuario.
     */
    @Query("SELECT " +
            "CONCAT(u.nombre, ' ', u.apellido) AS groupName, " +
            "d.nombre AS departmentName, " +
            "COALESCE(COUNT(t.id), 0L) AS totalTickets, " +
            "COALESCE(SUM(CASE WHEN t.fechaCierre IS NOT NULL THEN 1 ELSE 0 END), 0L) AS ticketsCerrados, " +
            "COALESCE(SUM(CASE WHEN t.fechaCierre IS NULL THEN 1 ELSE 0 END), 0L) AS ticketsActivos, " +
            // La misma simplificación para el AVG
            "AVG(CASE " +
            "    WHEN t.fechaCierre IS NOT NULL " +
            "    THEN CAST(TIMESTAMPDIFF(MINUTE, t.fechaCreacion, t.fechaCierre) AS double) / 60.0 " +
            "    ELSE NULL " +
            "END) AS avgResolutionTimeHoras " +
            "FROM Ticket t " +
            "JOIN t.usuarioAsignado u " +
            "JOIN u.departamento d " +
            "JOIN u.modulo m " +
            // AÑADIDO: El mismo filtro de fecha para los tickets
            "WHERE m.id = :moduloId AND t.isTrashed = false AND t.usuarioAsignado IS NOT NULL AND t.fechaCreacion >= :sevenDaysAgo " +
            "GROUP BY u.id, u.nombre, u.apellido, d.nombre, d.id " +
            "ORDER BY groupName")
    List<PerformanceProjection> getReporteUsuariosPorModulo(
            @Param("moduloId") Long moduloId,
            @Param("sevenDaysAgo") LocalDateTime sevenDaysAgo
    );

    Page<Ticket> findAllByDepartamentoNombreAndEstadoNombreAndFechaRespuestaIsNull(String departamentoNombre, String estadoNombre, Pageable pageable);
    Page<Ticket> findAllByIsTrashedFalseAndEstadoNombreAndFechaRespuestaIsNull (String estadoNombre, Pageable pageable);
}