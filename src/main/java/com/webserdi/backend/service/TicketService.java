package com.webserdi.backend.service;

import com.webserdi.backend.dto.RevisionDetailsDto;
import com.webserdi.backend.dto.TicketDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface TicketService {
    TicketDto createTicket(TicketDto dto);
    Page<TicketDto> getAllTickets(Pageable pageable, String departamento);
    Page<TicketDto> GetTicketsByUsuario(Pageable pageable, Long id, Long estadoId);
    Page<TicketDto> GetTicketsDashboard(Pageable pageable, Long id, Long estadoId);
    Page<TicketDto> getTickets(Pageable pageable, String filtro, String departamento);
    Page<TicketDto> getAllTrashedTickets(Pageable pageable, String filtro);
    Page<TicketDto> getTicketsByTema(Pageable pageable, String busqueda);
    TicketDto getTicketById(Long id);
    TicketDto updateTicket(Long id, TicketDto dto);
    TicketDto updateStatus(Long id, Long estadoId, Long usuarioId);
    TicketDto reassignUser(Long ticketId, Long usuarioId);
    TicketDto reassignDepartment(Long ticketId, Long usuarioId, Long incidenciaId, Long departamentoId);
    TicketDto updateCommitmentDate(Long ticketId, LocalDate fechaCompromiso);
    void deleteTicket(Long id);
    void restoreTicket(Long id);
    List<RevisionDetailsDto<TicketDto>> getTicketHistory(Long Id);
    void notAuthorized(Long ticketId, Long usuarioId);
    Page<TicketDto> findTicketsByCriteria(
            Long departamentoId, Long creadorId, Long asignadoId,
            Long estadoId, Long prioridadId, Boolean isAuthorized,
            Boolean hasResponse, String ubicacion, Pageable pageable
    );
    Page<TicketDto> getUnansweredTickets(Pageable pageable, String departamentoNombre, String estadoNombre, Long usuarioId);
}