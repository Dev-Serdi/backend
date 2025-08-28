package com.webserdi.backend.controller;

import com.webserdi.backend.dto.RevisionDetailsDto;
import com.webserdi.backend.dto.TicketDto;
import com.webserdi.backend.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Collections;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {

    private static final Logger log = LoggerFactory.getLogger(TicketController.class);
    private final TicketService ticketService;

    @PostMapping
    public ResponseEntity<?> createTicket(@RequestBody TicketDto dto) {
        try {
            TicketDto createdTicket = ticketService.createTicket(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdTicket);
        } catch (Exception e) {
            log.error("Error al crear el ticket: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Ocurrió un error inesperado al crear el ticket."));
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllTickets(
            @PageableDefault(size = 8) Pageable pageable,
            @RequestParam(required = false) String departamento) {
        try {
            return ResponseEntity.ok(ticketService.getAllTickets(pageable, departamento));
        } catch (Exception e) {
            log.error("Error al obtener todos los tickets: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Ocurrió un error inesperado al obtener los tickets."));
        }
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<?> getTicketsByUsuario(
            @PageableDefault(size = 8, sort = "fechaCreacion") Pageable pageable,
            @PathVariable Long id,
            @RequestParam(required = false) Long estadoId) {
        try {
            return ResponseEntity.ok(ticketService.GetTicketsByUsuario(pageable, id, estadoId));
        } catch (Exception e) {
            log.error("Error al obtener los tickets del usuario {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Ocurrió un error inesperado al obtener los tickets del usuario."));
        }
    }


    @PutMapping("/restore/{id}")
    public ResponseEntity<?> restoreTicket(@PathVariable Long id) {
        try {
            ticketService.restoreTicket(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Error al restaurar el ticket {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Ocurrió un error inesperado al restaurar el ticket."));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> getTicketsByTema(
            @PageableDefault(size = 8, sort = "fechaVencimiento") Pageable pageable,
            @RequestParam String busqueda) {
        try {
            return ResponseEntity.ok(ticketService.getTicketsByTema(pageable, busqueda));
        } catch (Exception e) {
            log.error("Error al buscar tickets con el término '{}': {}", busqueda, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Ocurrió un error inesperado al realizar la búsqueda."));
        }
    }

    @GetMapping
    public ResponseEntity<?> getTickets(
            @PageableDefault(size = 8) Pageable pageable,
            @RequestParam(required = false) String filtro,
            @RequestParam(required = false) String departamento) {
        try {
            return ResponseEntity.ok(ticketService.getTickets(pageable, filtro, departamento));
        } catch (Exception e) {
            log.error("Error al obtener tickets con filtro '{}' y departamento '{}': {}", filtro, departamento, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Ocurrió un error inesperado al obtener los tickets."));
        }
    }

    @GetMapping("/trashed")
    public ResponseEntity<?> getAllTrashedTickets(
            @PageableDefault(size = 8, sort = "fechaCreacion") Pageable pageable,
            @RequestParam(required = false) String filtro) {
        try {
            return ResponseEntity.ok(ticketService.getAllTrashedTickets(pageable, filtro));
        } catch (Exception e) {
            log.error("Error al obtener los tickets en la papelera: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Ocurrió un error inesperado al obtener los tickets de la papelera."));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTicketById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(ticketService.getTicketById(id));
        } catch (Exception e) {
            log.error("Error al obtener el ticket {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Ocurrió un error inesperado al obtener el ticket."));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTicket(@PathVariable Long id, @RequestBody TicketDto dto) {
        try {
            return ResponseEntity.ok(ticketService.updateTicket(id, dto));
        } catch (Exception e) {
            log.error("Error al actualizar el ticket {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Ocurrió un error inesperado al actualizar el ticket."));
        }
    }

    @PutMapping("/status/{id}")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestParam Long estadoId, @RequestParam Long usuarioId) {
        try {
            return ResponseEntity.ok(ticketService.updateStatus(id, estadoId, usuarioId));
        } catch (Exception e) {
            log.error("Error al actualizar el estado del ticket {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Ocurrió un error inesperado al actualizar el estado del ticket."));
        }
    }

    @PutMapping("/reassign/user/{ticketId}")
    public ResponseEntity<?> reassingUser (@PathVariable Long ticketId, @RequestParam Long usuarioId){
        try {
            return ResponseEntity.ok(ticketService.reassignUser(ticketId, usuarioId));
        } catch (Exception e) {
            log.error("Error al reasignar el ticket : {}", ticketId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Ocurrió un error al reasignar el ticket"));
        }
    }

    @PutMapping("/reassing/department/{ticketId}")
    public ResponseEntity<?> reassingDepartment (@PathVariable Long ticketId, @RequestParam Long usuarioId, @RequestParam Long incidenciaId, @RequestParam Long departamentoId){
        try {
            return ResponseEntity.ok(ticketService.reassignDepartment(ticketId, usuarioId, incidenciaId, departamentoId));
        } catch (Exception e) {
            log.error("Error al reasignar el ticket : {}", ticketId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Ocurrió un error al reasignar el ticket"));
        }
    }

    @PutMapping("/commitment-date/{ticketId}")
    public ResponseEntity<?> updateCommitmentDate (@PathVariable Long ticketId, @RequestBody LocalDate commitmentDate){
        try {
            return ResponseEntity.ok(ticketService.updateCommitmentDate(ticketId, commitmentDate));
        } catch (Exception e){
            log.error("Error al actualizar el ticket {}: {}", ticketId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Ocurrió un error al reasignar el ticket"));

        }
    }

    @DeleteMapping("/{ticketId}")
    public ResponseEntity<?> deleteTicket(@PathVariable Long ticketId) {
        try {
            ticketService.deleteTicket(ticketId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Error al eliminar el ticket {}: {}", ticketId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Ocurrió un error inesperado al eliminar el ticket."));
        }
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<?> getTicketHistory(@PathVariable Long id) {
        try {
            List<RevisionDetailsDto<TicketDto>> history = ticketService.getTicketHistory(id);
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            log.error("Error al obtener el historial del ticket {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Ocurrió un error inesperado al obtener el historial del ticket."));
        }
    }

    @PutMapping("/notauth/{ticketId}")
    public ResponseEntity<?> notAuthorized(@PathVariable Long ticketId, @RequestParam Long usuarioId) {
        log.info("PUT /api/tickets/notauth/{} - Solicitud para marcar un ticket como no autorizado por usuario ID: {}.", ticketId, usuarioId);
        try {
            ticketService.notAuthorized(ticketId, usuarioId);
            return ResponseEntity.ok(Collections.singletonMap("mensaje", "El ticket ha sido marcado como no autorizado exitosamente."));
        } catch (Exception e) {
            log.error("Error al marcar el ticket {} como no autorizado: {}", ticketId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Ocurrió un error inesperado al procesar la solicitud."));
        }
    }
    @GetMapping("/dashboard/user/{id}")
    public ResponseEntity<?> getTicketsDashboard(
            @PageableDefault(size = 8, sort = "fechaCreacion") Pageable pageable,
            @PathVariable Long id,
            @RequestParam(required = false) Long estadoId) {
        try {
            return ResponseEntity.ok(ticketService.GetTicketsDashboard(pageable, id, estadoId));
        } catch (Exception e) {
            log.error("Error al obtener el dashboard de tickets del usuario {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Ocurrió un error inesperado al obtener el dashboard de tickets."));
        }
    }
    @GetMapping("/reporte-filtrado")
    public ResponseEntity<Page<TicketDto>> getFilteredTickets(
            @RequestParam(required = false) Long departamentoId,
            @RequestParam(required = false) Long creadorId,
            @RequestParam(required = false) Long asignadoId,
            @RequestParam(required = false) Long estadoId,
            @RequestParam(required = false) Long prioridadId,
            @RequestParam(required = false) Boolean isAuthorized,
            @RequestParam(required = false) Boolean hasResponse,
            @RequestParam(required = false) String ubicacion,
            Pageable pageable) {
        Page<TicketDto> tickets = ticketService.findTicketsByCriteria(
                departamentoId, creadorId, asignadoId, estadoId, prioridadId,
                isAuthorized, hasResponse, ubicacion, pageable
        );
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/unanswered")
    public ResponseEntity<?> getUnansweredTickets(
            @PageableDefault(size = 8, sort = "fechaCreacion") Pageable pageable,
            @RequestParam(required = false) String departamento,
            @RequestParam(required = false) String estadoNombre,
            @RequestParam(required = false) Long usuarioId) {
        try {
            return ResponseEntity.ok(ticketService.getUnansweredTickets(pageable, departamento, estadoNombre,usuarioId));
        } catch (Exception e) {
            log.error("Error al obtener tickets sin respuesta: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Ocurrió un error inesperado al obtener los tickets."));
        }
    }
}