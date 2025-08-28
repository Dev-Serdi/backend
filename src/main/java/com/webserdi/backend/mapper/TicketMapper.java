package com.webserdi.backend.mapper;

import com.webserdi.backend.dto.TicketDto;
import com.webserdi.backend.entity.Ticket;
import org.springframework.stereotype.Component;

/**
 * Mapper para convertir entre entidades {@link Ticket} y DTOs {@link TicketDto}.
 */
@Component
public class TicketMapper {

    /**
     * Convierte una entidad {@link Ticket} a un {@link TicketDto}.
     *
     * @param ticket La entidad Ticket a convertir.
     * @return El TicketDto resultante, o null si la entidad de entrada es null.
     */
    public TicketDto toDto(Ticket ticket) {
        if (ticket == null) {
            return null;
        }

        TicketDto ticketDto = new TicketDto();
        ticketDto.setId(ticket.getId());
        ticketDto.setTema(ticket.getTema());
        ticketDto.setDescripcion(ticket.getDescripcion());
        ticketDto.setCodigo(ticket.getCodigo());
        ticketDto.setIsTrashed(ticket.getIsTrashed());
        ticketDto.setIsAttended(ticket.getIsAttended());
        ticketDto.setIsAuthorized(ticket.getIsAuthorized());
        ticketDto.setFechaCreacion(ticket.getFechaCreacion());
        ticketDto.setFechaActualizacion(ticket.getFechaActualizacion());
        ticketDto.setFechaCompromiso(ticket.getFechaCompromiso());
        ticketDto.setFechaVencimiento(ticket.getFechaVencimiento());
        ticketDto.setFechaCierre(ticket.getFechaCierre());
        ticketDto.setFechaRespuesta(ticket.getFechaRespuesta());

        if (ticket.getUbicacion() != null) ticketDto.setUbicacion(ticket.getUbicacion());

        if (ticket.getChat() != null) ticketDto.setChatId(ticket.getChat().getId());

        if (ticket.getUsuarioCreador() != null) {
            ticketDto.setUsuarioCreador(ticket.getUsuarioCreador().getId());
            ticketDto.setUsuarioCreadorNombres(
                    ticket.getUsuarioCreador().getNombre() + " " + ticket.getUsuarioCreador().getApellido()
            );
        }

        if (ticket.getUsuarioAsignado() != null) {
            ticketDto.setUsuarioAsignado(ticket.getUsuarioAsignado().getId());
            ticketDto.setUsuarioAsignadoNombres(
                    ticket.getUsuarioAsignado().getNombre() + " " + ticket.getUsuarioAsignado().getApellido()
            );
        } else {
            ticketDto.setUsuarioAsignadoNombres("No asignado"); // Opcional: valor por defecto
        }

        if (ticket.getDepartamento() != null) {
            ticketDto.setDepartamento(ticket.getDepartamento().getId());
            ticketDto.setDepartamentoNombre(ticket.getDepartamento().getNombre());
        }

        if (ticket.getFuente() != null) {
            ticketDto.setFuente(ticket.getFuente().getId());
            ticketDto.setFuenteNombre(ticket.getFuente().getNombre());
        }

        if (ticket.getIncidencia() != null) {
            ticketDto.setIncidencia(ticket.getIncidencia().getId());
            ticketDto.setIncidenciaNombre(ticket.getIncidencia().getNombre());
        }

        if (ticket.getPrioridad() != null) {
            ticketDto.setPrioridad(ticket.getPrioridad().getId());
            ticketDto.setPrioridadNombre(ticket.getPrioridad().getNombre());
        }

        if (ticket.getEstado() != null) {
            ticketDto.setEstado(ticket.getEstado().getId());
            ticketDto.setEstadoNombre(ticket.getEstado().getNombre());
        }

        return ticketDto;
    }

    /**
     * Convierte un {@link TicketDto} a una entidad {@link Ticket}.
     * Los campos como 'codigo', 'fechaCreacion', 'fechaActualizacion' y las entidades relacionadas
     * se establecen generalmente en la capa de servicio.
     *
     * @param dto El TicketDto a convertir.
     * @return La entidad Ticket resultante, o null si el DTO de entrada es null.
     */
    public Ticket toEntity(TicketDto dto) {
        if (dto == null) {
            return null;
        }

        Ticket ticket = new Ticket();
        // El ID se establece si es una actualización, sino es null para creación
        ticket.setId(dto.getId());
        ticket.setTema(dto.getTema());
        ticket.setDescripcion(dto.getDescripcion());
        ticket.setFechaVencimiento(dto.getFechaVencimiento());

        // Campos que se manejan en el servicio:
        // ticket.setCodigo(dto.getCodigo()); // Se genera en el servicio
        // ticket.setIsTrashed(dto.getIsTrashed()); // Se maneja en el servicio
        // ticket.setFechaCreacion(dto.getFechaCreacion()); // Se genera automáticamente
        // ticket.setFechaActualizacion(dto.getFechaActualizacion()); // Se genera automáticamente
        // Las entidades relacionadas (Usuario, Departamento, etc.) se establecen en el servicio.

        return ticket;
    }
}