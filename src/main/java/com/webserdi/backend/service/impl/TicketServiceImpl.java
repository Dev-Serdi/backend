package com.webserdi.backend.service.impl;

import com.webserdi.backend.config.audit.CustomRevisionEntity;
import com.webserdi.backend.dto.TicketDto;
import com.webserdi.backend.entity.*;
import com.webserdi.backend.exception.ResourceNotFoundException;
import com.webserdi.backend.exception.BusinessException;
import com.webserdi.backend.mapper.TicketMapper;
import com.webserdi.backend.repository.*;
import com.webserdi.backend.service.NotificationService;
import com.webserdi.backend.service.TicketService;
import jakarta.persistence.EntityManager;
import com.webserdi.backend.dto.RevisionDetailsDto;
import lombok.RequiredArgsConstructor;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils; // Para validaciones de String

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects; // Para Objects.requireNonNull

import static java.lang.Integer.parseInt;

@Service
@RequiredArgsConstructor // Lombok genera el constructor con todas las dependencias finales
public class TicketServiceImpl implements TicketService {

    private static final Logger logger = LoggerFactory.getLogger(TicketServiceImpl.class);

    // Repositorios y Mappers
    private final TicketRepository ticketRepository;
    private final TicketSpecification ticketSpecification;
    private final UsuarioRepository usuarioRepository;
    private final DepartamentoRepository departamentoRepository;
    private final FuenteRepository fuenteRepository;
    private final IncidenciaRepository incidenciaRepository;
    private final PrioridadRepository prioridadRepository;
    private final EstadoRepository estadoRepository;
    private final TicketMapper ticketMapper;
    private final NotificationService notificationService;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final EntityManager entityManager;
    private final GestorNotificacionesImpl gestorNotificaciones;


    @Override
    @Transactional(readOnly = true)
    public List<RevisionDetailsDto<TicketDto>> getTicketHistory(Long ticketId){
        AuditReader auditReader = AuditReaderFactory.get(entityManager);

        List<Number> revisionNumbers = auditReader.getRevisions(Ticket.class, ticketId);

        List<RevisionDetailsDto<TicketDto>> history = new ArrayList<>();
        for (Number rev : revisionNumbers) {
            Ticket historicTicket = auditReader.find(Ticket.class, ticketId, rev);
            TicketDto ticketDto = ticketMapper.toDto(historicTicket);

            CustomRevisionEntity revisionEntity = auditReader.findRevision(CustomRevisionEntity.class, rev);
            Date revisionDate = auditReader.getRevisionDate(rev);
            LocalDateTime revisionTimestamp = revisionDate.toInstant().atZone(ZoneOffset.UTC).toLocalDateTime();

            history.add(new RevisionDetailsDto<>(rev.intValue(), revisionTimestamp, revisionEntity.getEmail(), ticketDto));
        }
        return history;
    }

    @Override
    @Transactional
    public TicketDto createTicket(TicketDto dto) {
        logger.info("Iniciando creación de ticket con DTO: {}", dto);
        validateTicketDtoForCreation(dto);

        Ticket ticket = ticketMapper.toEntity(dto);

        ticket.setIsTrashed(false);
        try {
            ticket.setCodigo(generateNextCodigo());
        } catch (IllegalStateException e) {
            logger.error("Error generando código para el nuevo ticket", e);
            throw new BusinessException("Error generando código del ticket: " + e.getMessage());
        }
        setTicketRelationships(ticket, dto);

        ZonedDateTime utcMinus7Time = ZonedDateTime.now(ZoneOffset.UTC)
                .withZoneSameInstant(ZoneOffset.ofHours(-7));
        LocalDateTime fechaRegistro = utcMinus7Time.toLocalDateTime();
        int prioridad = Integer.parseInt(String.valueOf(ticket.getPrioridad().getId()));
        LocalDate fechaVencimiento = getVencimiento(fechaRegistro, prioridad);
        ticket.setFechaCreacion(fechaRegistro);
        ticket.setFechaVencimiento(fechaVencimiento);
        Ticket savedTicket = ticketRepository.save(ticket);
        logger.info("Ticket creado exitosamente con ID: {} y Código: {}", savedTicket.getId(), savedTicket.getCodigo());

        logger.info("Enviando notificación estándar para el ticket {}", savedTicket.getCodigo());
        gestorNotificaciones.dispatch(new EventoNotificacionServiceImpl(TipoNotificacion.NUEVO_TICKET_ASIGNADO, savedTicket),null,null);

        if (savedTicket.getUsuarioAsignado() != null) {
            logger.info("Enviando notificación WebSocket al usuario asignado: {}", savedTicket.getUsuarioAsignado().getEmail());
            TicketDto notificationDto = ticketMapper.toDto(savedTicket);
            simpMessagingTemplate.convertAndSendToUser(
                    savedTicket.getUsuarioAsignado().getEmail(),
                    "/queue/tickets",
                    notificationDto
            );
        }
        return ticketMapper.toDto(savedTicket);
    }

    private void validateTicketDtoForCreation(TicketDto dto) {
        Objects.requireNonNull(dto, "El DTO del ticket no puede ser nulo.");
        if (!StringUtils.hasText(dto.getTema())) {
            throw new ResourceNotFoundException("El tema del ticket es obligatorio.");
        }
        if (!StringUtils.hasText(dto.getDescripcion())) {
            throw new ResourceNotFoundException("La descripción del ticket es obligatoria.");
        }
        if (dto.getUsuarioCreador() == null) {
            throw new ResourceNotFoundException("El ID del usuario creador es obligatorio.");
        }
        if (dto.getDepartamento() == null) {
            throw new ResourceNotFoundException("El ID del departamento es obligatorio.");
        }
        if (dto.getIncidencia() == null) {
            throw new ResourceNotFoundException("El ID de la incidencia es obligatorio.");
        }
        if (dto.getPrioridad() == null) {
            throw new ResourceNotFoundException("El ID de la prioridad es obligatorio.");
        }
        if (dto.getEstado() == null) {
            throw new ResourceNotFoundException("El ID del estado es obligatorio.");
        }
        if (dto.getFuente() == null) { 
            throw new ResourceNotFoundException("El ID de la fuente es obligatorio.");
        }
    }

    private void setTicketRelationships(Ticket ticket, TicketDto dto) {
        ticket.setUsuarioCreador(usuarioRepository.findById(dto.getUsuarioCreador())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario creador no encontrado con ID: " + dto.getUsuarioCreador())));

        if (dto.getUsuarioAsignado() != null) {
            ticket.setUsuarioAsignado(usuarioRepository.findById(dto.getUsuarioAsignado())
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario asignado no encontrado con ID: " + dto.getUsuarioAsignado())));
        } else {
            ticket.setUsuarioAsignado(null); 
        }

        ticket.setDepartamento(departamentoRepository.findById(dto.getDepartamento())
                .orElseThrow(() -> new ResourceNotFoundException("Departamento no encontrado con ID: " + dto.getDepartamento())));

        ticket.setFuente(fuenteRepository.findById(dto.getFuente())
                .orElseThrow(() -> new ResourceNotFoundException("Fuente no encontrada con ID: " + dto.getFuente())));

        Incidencia incidencia = incidenciaRepository.findById(dto.getIncidencia())
                .orElseThrow(() -> new ResourceNotFoundException("Incidencia no encontrada con ID: " + dto.getIncidencia()));
        if (!incidencia.getDepartamento().getId().equals(ticket.getDepartamento().getId())) {
            throw new ResourceNotFoundException(
                    String.format("La incidencia '%s' (ID: %d) no pertenece al departamento '%s' (ID: %d).",
                            incidencia.getNombre(), incidencia.getId(),
                            ticket.getDepartamento().getNombre(), ticket.getDepartamento().getId())
            );
        }
        ticket.setIncidencia(incidencia);
        if (ticket.getUsuarioCreador().getUbicacion() != null) ticket.setUbicacion(ticket.getUsuarioCreador().getUbicacion());

        ticket.setEstado(estadoRepository.findById(dto.getEstado())
                .orElseThrow());

        ticket.setPrioridad(prioridadRepository.findById(dto.getPrioridad())
                .orElseThrow(() -> new ResourceNotFoundException("Prioridad no encontrada con ID: " + dto.getPrioridad())));

        String ubicacionUsuario = ticket.getUsuarioCreador().getUbicacion();
        ticket.setUbicacion(ubicacionUsuario != null ? ubicacionUsuario.trim() : "");
    }

    @Override
    @Transactional
    public TicketDto updateTicket(Long id, TicketDto dto) {
        logger.info("Iniciando actualización de ticket con ID: {} y DTO: {}", id, dto);
        Objects.requireNonNull(dto, "El DTO del ticket no puede ser nulo para la actualización.");

        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket no encontrado con ID para actualizar: " + id));

        if (!StringUtils.hasText(dto.getTema())) {
            throw new ResourceNotFoundException("El tema del ticket es obligatorio para la actualización.");
        }
        if (!StringUtils.hasText(dto.getDescripcion())) {
            throw new ResourceNotFoundException("La descripción del ticket es obligatoria para la actualización.");
        }
        ticket.setTema(dto.getTema());
        ticket.setDescripcion(dto.getDescripcion());
        ticket.setFechaCompromiso(dto.getFechaCompromiso());
        ZonedDateTime utcMinus7Time = ZonedDateTime.now(ZoneOffset.UTC)
                .withZoneSameInstant(ZoneOffset.ofHours(-7));
        LocalDateTime fechaRegistro = utcMinus7Time.toLocalDateTime();
        ticket.setFechaActualizacion(fechaRegistro);
        int prioridad = Integer.parseInt(String.valueOf(dto.getPrioridad()));
        LocalDate fechaVencimiento = getVencimiento(ticket.getFechaCreacion(), prioridad);
        ticket.setFechaVencimiento(fechaVencimiento);

        if (dto.getUsuarioAsignado() != null) {
            if (ticket.getUsuarioAsignado() == null || !dto.getUsuarioAsignado().equals(ticket.getUsuarioAsignado().getId())) {
                ticket.setUsuarioAsignado(usuarioRepository.findById(dto.getUsuarioAsignado())
                        .orElseThrow(() -> new ResourceNotFoundException("Usuario asignado no encontrado con ID: " + dto.getUsuarioAsignado())));
            }
        }

        if (dto.getDepartamento() != null && (ticket.getDepartamento() == null || !dto.getDepartamento().equals(ticket.getDepartamento().getId()))) {
            ticket.setDepartamento(departamentoRepository.findById(dto.getDepartamento())
                    .orElseThrow(() -> new ResourceNotFoundException("Departamento no encontrado con ID: " + dto.getDepartamento())));
            if (dto.getIncidencia() == null) {
                throw new ResourceNotFoundException("Si se cambia el departamento, se debe especificar una nueva incidencia válida para ese departamento.");
            }
        }

        if (dto.getIncidencia() != null && (ticket.getIncidencia() == null || !dto.getIncidencia().equals(ticket.getIncidencia().getId()))) {
            Incidencia nuevaIncidencia = incidenciaRepository.findById(dto.getIncidencia())
                    .orElseThrow(() -> new ResourceNotFoundException("Incidencia no encontrada con ID: " + dto.getIncidencia()));
            if (!nuevaIncidencia.getDepartamento().getId().equals(ticket.getDepartamento().getId())) {
                throw new ResourceNotFoundException(
                        String.format("La nueva incidencia '%s' no pertenece al departamento '%s' del ticket.",
                                nuevaIncidencia.getNombre(), ticket.getDepartamento().getNombre())
                );
            }
            ticket.setIncidencia(nuevaIncidencia);
        } else if (dto.getDepartamento() != null && dto.getIncidencia() == null) {
            throw new ResourceNotFoundException("Se requiere una incidencia al cambiar el departamento.");
        }

        if (dto.getFuente() != null && (ticket.getFuente() == null || !dto.getFuente().equals(ticket.getFuente().getId()))) {
            ticket.setFuente(fuenteRepository.findById(dto.getFuente())
                    .orElseThrow(() -> new ResourceNotFoundException("Fuente no encontrada con ID: " + dto.getFuente())));
        }

        if (dto.getPrioridad() != null && (ticket.getPrioridad() == null || !dto.getPrioridad().equals(ticket.getPrioridad().getId()))) {
            ticket.setPrioridad(prioridadRepository.findById(dto.getPrioridad())
                    .orElseThrow(() -> new ResourceNotFoundException("Prioridad no encontrada con ID: " + dto.getPrioridad())));
        }

        if (dto.getEstado() != null && (ticket.getEstado() == null || !dto.getEstado().equals(ticket.getEstado().getId()))) {
            ticket.setEstado(estadoRepository.findById(dto.getEstado())
                    .orElseThrow(() -> new ResourceNotFoundException("Estado no encontrado con ID: " + dto.getEstado())));
        }

        Ticket updatedTicket = ticketRepository.save(ticket);
        gestorNotificaciones.dispatch(new EventoNotificacionServiceImpl(TipoNotificacion.TICKET_MODIFICADO, updatedTicket), null,null);
        logger.info("Ticket con ID: {} actualizado exitosamente.", updatedTicket.getId());
        return ticketMapper.toDto(updatedTicket);
    }

    @Override
    public TicketDto updateStatus(Long id, Long estadoId, Long usuarioId) {
        Ticket updatedTicket = ticketRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Ticket no encontrado con ID: " + id));
        Estado nuevoEstado = estadoRepository.findById(estadoId)
                .orElseThrow(() -> new ResourceNotFoundException("Estado no encontrado con ID: " + estadoId));
        updatedTicket.setEstado(nuevoEstado);
        Usuario usuarioCerrar = usuarioRepository.findById(usuarioId).orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + usuarioId));
        updatedTicket.setIsAuthorized(true);
        updatedTicket.setFechaActualizacion(getCurrentTime());

        if (estadoId==1) updatedTicket.setFechaCierre(null);
        else {
            updatedTicket.setUsuarioCerrar(usuarioCerrar);
            updatedTicket.setFechaCierre(getCurrentTime());
        }

        logger.info("Transmitiendo notificacion al usuario asignado");
        gestorNotificaciones.dispatch(new EventoNotificacionServiceImpl(TipoNotificacion.CAMBIO_ESTADO_TICKET, updatedTicket),null,null);
        ticketRepository.save(updatedTicket);
        return ticketMapper.toDto(updatedTicket);
    }

    @Override
    @Transactional
    public void notAuthorized(Long ticketId, Long usuarioId) {
        logger.info("Iniciando proceso para marcar ticket ID: {} como NO AUTORIZADO por usuario ID: {}", ticketId, usuarioId,null);

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket no encontrado con ID: " + ticketId));

        Usuario usuarioResponsable = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario responsable no encontrado con ID: " + usuarioId));

        ticket.setIsAuthorized(false);

        ticket.setEstado(estadoRepository.findById(3L)
                .orElseThrow(() -> new ResourceNotFoundException("Estado 'Cerrado' (ID: 3) no encontrado, verificar base de datos.")));

        ticket.setFechaCierre(getCurrentTime());
        ticket.setFechaActualizacion(getCurrentTime());
        ticket.setUsuarioCerrar(usuarioResponsable);

        ticketRepository.save(ticket);
        gestorNotificaciones.dispatch(new EventoNotificacionServiceImpl(TipoNotificacion.TICKET_NO_AUTORIZADO, ticket),null,null);
        logger.info("Ticket con ID {} marcado como NO AUTORIZADO exitosamente.", ticketId);
    }

    @Override
    public Page<TicketDto> GetTicketsDashboard(Pageable pageable,Long id, Long estadoId) {
        Page<Ticket> ticketsPage;
        if (estadoId !=0)
        {
            ticketsPage = ticketRepository.findAllByUsuarioCreadorIdAndEstadoId(pageable, id, estadoId);
            return ticketsPage.map(ticketMapper::toDto);
        }
        ticketsPage = ticketRepository.findAllByUsuarioCreadorId(pageable,id);
        return ticketsPage.map(ticketMapper::toDto);
    }

    @Override
    public Page<TicketDto> findTicketsByCriteria(
            Long departamentoId, Long creadorId, Long asignadoId,
            Long estadoId, Long prioridadId, Boolean isAuthorized,
            Boolean hasResponse, String ubicacion, Pageable pageable) {

        Specification<Ticket> spec = ticketSpecification.getTickets(
                departamentoId, creadorId, asignadoId, estadoId, prioridadId,
                isAuthorized, hasResponse, ubicacion
        );
        return ticketRepository.findAll(spec, pageable).map(ticketMapper::toDto);
    }

    @Override
    public Page<TicketDto> getUnansweredTickets(Pageable pageable, String departamentoNombre, String estadoNombre, Long usuarioId) {
        Page<Ticket> ticketsPage = findUnansweredTickets(pageable, departamentoNombre, estadoNombre, usuarioId);
        return ticketsPage.map(ticketMapper::toDto);
    }
    private Page<Ticket> findUnansweredTickets(Pageable pageable, String departamentoNombre, String estadoNombre, Long usuarioId) {
        if (StringUtils.hasText(departamentoNombre)) {
            if (StringUtils.hasText(estadoNombre)) {
                return ticketRepository.findAllByDepartamentoNombreAndEstadoNombreAndFechaRespuestaIsNull(departamentoNombre, estadoNombre, pageable);
            }
            return ticketRepository.findAllByIsTrashedFalseAndDepartamentoNombreAndFechaRespuestaIsNull(departamentoNombre, pageable);
        }
        if (StringUtils.hasText(estadoNombre)) {
            return ticketRepository.findAllByIsTrashedFalseAndEstadoNombreAndFechaRespuestaIsNull(estadoNombre, pageable);
        }
        if (usuarioId != null) {
            return ticketRepository.findAllByIsTrashedFalseAndUsuarioAsignadoIdAndFechaRespuestaIsNull(usuarioId, pageable);
        }
        return ticketRepository.findAllByIsTrashedFalseAndFechaRespuestaIsNull(pageable);
    }

    @Override
    public TicketDto reassignUser(Long ticketId, Long usuarioId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket no encontrado con ID: " + ticketId));
        Usuario nuevoUsuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + usuarioId));
        if(Objects.equals(ticket.getUsuarioAsignado().getId(), usuarioId)) return ticketMapper.toDto(ticket);
        gestorNotificaciones.dispatch(new EventoNotificacionServiceImpl(TipoNotificacion.REASIGNACION_USUARIO_TICKET, ticket), nuevoUsuario,null);
        ticket.setUsuarioAsignado(usuarioRepository.findById(usuarioId).orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + usuarioId)));
        ticket.setFechaActualizacion(getCurrentTime());
        ticketRepository.save(ticket);
        return ticketMapper.toDto(ticket);
    }

    @Override
    public TicketDto reassignDepartment(Long ticketId, Long usuarioId, Long incidenciaId, Long departamentoId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(()-> new ResourceNotFoundException("Ticket no encontrado"));
        Usuario nuevoUsuario = usuarioRepository.findById(usuarioId).orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + usuarioId));
        ticket.setIncidencia(incidenciaRepository.findById(incidenciaId).orElseThrow(() -> new ResourceNotFoundException("Incidencia no encontrada")));
        ticket.setDepartamento(departamentoRepository.findById(departamentoId).orElseThrow(() -> new ResourceNotFoundException("Departamento no encontrado")));
        notificationService.sendReassignedDepartment(ticket, nuevoUsuario);//Le mandamos el ticket sin modificar y seguidamente el correo del nuevo usuario para notificar a los 3 usuarios que se reasignó el ticket
        ticket.setFechaActualizacion(getCurrentTime());
        ticket.setUsuarioAsignado(nuevoUsuario);
        ticketRepository.save(ticket);
        gestorNotificaciones.dispatch(new EventoNotificacionServiceImpl(TipoNotificacion.REASIGNACION_DEPARTAMENTO_TICKET, ticket), null,null);
        return ticketMapper.toDto(ticket);
    }

    @Override
    public TicketDto updateCommitmentDate(Long ticketId, LocalDate commitmentDate) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket no encontrado con ID: " + ticketId));
        ticket.setFechaCompromiso(commitmentDate);
        ticket.setFechaActualizacion(getCurrentTime());
        gestorNotificaciones.dispatch(new EventoNotificacionServiceImpl(TipoNotificacion.FECHA_COMPROMISO_ASIGNADA,ticket),null,null);
        ticketRepository.save(ticket);
        return ticketMapper.toDto(ticket);
    }

    private LocalDateTime getCurrentTime() {
        return ZonedDateTime.now(ZoneOffset.UTC)

                .withZoneSameInstant(ZoneOffset.ofHours(-7))
                .toLocalDateTime();
    }

    @Override
    public Page<TicketDto> getAllTickets(Pageable pageable, String departamentoNombre) {
        Page<Ticket> ticketsPage;
        if (StringUtils.hasText(departamentoNombre)) {
            ticketsPage = ticketRepository.findAllByDepartamentoNombreAndIsTrashedFalseOrderByFechaCreacionDesc(departamentoNombre, pageable);
            return ticketsPage.map(ticketMapper::toDto);
        }
        ticketsPage = ticketRepository.findAllByIsTrashedFalseOrderByFechaCreacionDesc(pageable);
        return ticketsPage.map(ticketMapper::toDto);
    }

    @Override
    public Page<TicketDto> GetTicketsByUsuario(Pageable pageable, Long id, Long estadoId) {
        Page<Ticket> ticketsPage;
        if(estadoId != 0)//Muestra los tickets del usuario según el estado "en-proceso", "completados", "cerrados"
        {
            ticketsPage = ticketRepository.findAllByUsuarioAsignadoIdAndEstadoId(pageable, id, estadoId);
            return ticketsPage.map(ticketMapper::toDto);
        }
        ticketsPage = ticketRepository.findAllByUsuarioAsignadoId(pageable,id);
        return ticketsPage.map(ticketMapper::toDto);
    }


    @Override
    public Page<TicketDto> getTickets(Pageable pageable, String estadoNombre, String departamentoNombre) {
        logger.debug("Obteniendo tickets. Pageable: {}, Estado: {}, Departamento: {}", pageable, estadoNombre, departamentoNombre);
        Page<Ticket> ticketsPage;
        if (!StringUtils.hasText(estadoNombre)) {
            ticketsPage = ticketRepository.findAllByDepartamentoNombreAndIsTrashedFalseOrderByFechaCreacionDesc(departamentoNombre, pageable);
            return ticketsPage.map(ticketMapper::toDto);
        }
        if (StringUtils.hasText(departamentoNombre)) {
            ticketsPage = ticketRepository.findAllByEstadoNombreAndDepartamentoNombreAndIsTrashedFalse(estadoNombre, departamentoNombre, pageable);
        } else {
            ticketsPage = ticketRepository.findAllByEstadoNombreAndIsTrashedFalse(estadoNombre, pageable);
        }
        return ticketsPage.map(ticketMapper::toDto);
    }

    @Override
    public Page<TicketDto> getAllTrashedTickets(Pageable pageable, String filtro) {
        logger.debug("Obteniendo tickets en la papelera. Pageable: {}", pageable);
        Page<Ticket> ticketsPage = ticketRepository.findAllByIsTrashedTrue(pageable);
        return ticketsPage.map(ticketMapper::toDto);
    }

    @Override
    public Page<TicketDto> getTicketsByTema(Pageable pageable, String busqueda) {
        logger.debug("Obteniendo tickets por tema. Pageable: {}, Busqueda: {}", pageable, busqueda);
        Page<Ticket> ticketsPage = ticketRepository.findAllByTemaContainsIgnoreCaseAndIsTrashedFalseOrCodigoContainsIgnoreCaseAndIsTrashedFalse(busqueda,busqueda, pageable);
        return ticketsPage.map(ticketMapper::toDto);
    }

    @Override
    public TicketDto getTicketById(Long id) {
        logger.debug("Obteniendo ticket por ID: {}", id);
        return ticketRepository.findById(id)
                .map(ticketMapper::toDto)
                .orElseThrow(() -> {
                    logger.warn("Ticket no encontrado con ID: {}", id);
                    return new ResourceNotFoundException("Ticket no encontrado con ID: " + id);
                });
    }

    @Override
    @Transactional
    public void deleteTicket(Long id) {
        logger.info("Marcando ticket con ID: {} como eliminado (en papelera).", id);
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket no encontrado para eliminar con ID: " + id));

        if (ticket.getIsTrashed()) {
            logger.warn("El ticket con ID: {} ya está en la papelera.", id);
            return;
        }
        ticket.setIsTrashed(true);
        ticketRepository.save(ticket);
        logger.info("Ticket con ID: {} movido a la papelera.", id);
    }

    @Override
    @Transactional
    public void restoreTicket(Long id) {
        logger.info("Restaurando ticket con ID: {} desde la papelera.", id);
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket no encontrado para restaurar con ID: " + id));

        if (!ticket.getIsTrashed()) {
            logger.warn("El ticket con ID: {} no está en la papelera, no se puede restaurar.", id);
            return;
        }
        ticket.setIsTrashed(false);
        ticketRepository.save(ticket);
        logger.info("Ticket con ID: {} restaurado.", id);
    }

    private String generateNextCodigo() {
        String maxCodigo = ticketRepository.findMaxCodigo();
        logger.debug("Último código máximo encontrado: {}", maxCodigo);

        if (maxCodigo == null) {
            logger.info("No se encontró código previo, generando el primero: ATN-AA-0001");
            return "ATN-AA-0001";
        }

        String[] parts = maxCodigo.split("-");
        if (parts.length != 3 || !parts[0].equals("ATN")) {
            logger.error("Formato de código inválido encontrado en la base de datos: {}", maxCodigo);
            throw new IllegalStateException("Formato de código inválido en la base de datos: " + maxCodigo);
        }

        String letras = parts[1];
        int numero;
        try {
            numero = parseInt(parts[2]);
        } catch (NumberFormatException e) {
            logger.error("Parte numérica del código inválida: {} en código {}", parts[2], maxCodigo, e);
            throw new IllegalStateException("Parte numérica del código inválida: " + parts[2], e);
        }

        if (numero < 9999) {
            numero++;
        } else {
            letras = incrementarLetras(letras);
            numero = 1;
        }

        String nuevoCodigo = String.format("ATN-%s-%04d", letras, numero);
        logger.info("Nuevo código generado: {}", nuevoCodigo);
        return nuevoCodigo;
    }

    private String incrementarLetras(String letras) {
        if (letras == null || letras.length() != 2) {
            logger.error("Formato de letras inválido para incrementar: {}", letras);
            throw new IllegalArgumentException("El string de letras debe tener longitud 2.");
        }
        char[] chars = letras.toCharArray();

        chars[1]++;
        if (chars[1] > 'Z') {
            chars[1] = 'A';
            chars[0]++;
            if (chars[0] > 'Z') {
                logger.error("Límite máximo de combinaciones de letras (ZZ) alcanzado.");
                throw new IllegalStateException("Límite máximo de códigos (ATN-ZZ-9999) alcanzado.");
            }
        }
        return new String(chars);
    }

    private LocalDate getVencimiento(LocalDateTime fechaRegistro, int prioridad){
        LocalDate fechaVencimiento;
        switch (prioridad){
            case 1:
                fechaVencimiento = fechaRegistro.toLocalDate().plusDays(1);
                break;
            case 2:
                fechaVencimiento = fechaRegistro.toLocalDate().plusDays(3);
                break;
            case 3:
                fechaVencimiento = fechaRegistro.toLocalDate().plusDays(8);
                break;
            default:
                fechaVencimiento = fechaRegistro.toLocalDate().plusDays(5);
                logger.warn("Prioridad desconocida: {}. Se asigna fecha de vencimiento por defecto (+5 días).", prioridad);
                break;
        }
        return fechaVencimiento;
    }
}
