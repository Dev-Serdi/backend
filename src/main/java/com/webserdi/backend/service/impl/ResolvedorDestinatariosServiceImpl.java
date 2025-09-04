package com.webserdi.backend.service.impl;

import com.webserdi.backend.entity.TipoNotificacion;
import com.webserdi.backend.entity.Usuario;
import com.webserdi.backend.service.ResolvedorDestinatariosService;
import org.springframework.stereotype.Service;
import com.webserdi.backend.entity.Ticket;
import com.webserdi.backend.repository.UsuarioRepository;
import java.util.HashSet;
import java.util.Set;
import java.util.Optional;

@Service
public class ResolvedorDestinatariosServiceImpl implements ResolvedorDestinatariosService {
    private final UsuarioRepository usuarioRepository;

    public ResolvedorDestinatariosServiceImpl(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Determina los destinatarios de una notificación basada en el tipo de evento y las reglas de negocio.
     * @param evento El evento de notificación.
     * @return Un Set de Usuarios que deben ser notificados.
     */
    @Override
    public Set<Usuario> resolver(EventoNotificacionServiceImpl evento) {
        Set<Usuario> destinatarios = new HashSet<>();

        // La mayoría de los eventos están relacionados con un Ticket
        Ticket ticket = (evento.getData() instanceof Ticket) ? (Ticket) evento.getData() : null;
        Usuario usuarioData = (evento.getData() instanceof Usuario) ? (Usuario) evento.getData() : null;

        switch (evento.getTipo()) {
            case NUEVO_TICKET_ASIGNADO: // Para agentes
                Optional.ofNullable(ticket.getUsuarioAsignado()).ifPresent(destinatarios::add);
                break;

            case NUEVO_TICKET_CREADO: // Para administradores
                destinatarios.addAll(getAdmins());
                break;

            case FECHA_COMPROMISO_ASIGNADA:
            case CAMBIO_ESTADO_TICKET:
            case TICKET_MODIFICADO:
            case REASIGNACION_USUARIO_TICKET:
            case REASIGNACION_DEPARTAMENTO_TICKET:
                // Estas reglas aplican a varios eventos de ticket
                if (ticket != null) {
                    Optional.ofNullable(ticket.getUsuarioCreador()).ifPresent(destinatarios::add); // Creador (Usuario)
                    Optional.ofNullable(ticket.getUsuarioAsignado()).ifPresent(destinatarios::add); // Asignado (Agente)
                    destinatarios.addAll(getAdmins()); // Todos los admins
                }
                break;

            case TICKET_NO_AUTORIZADO:
                if (ticket != null) {
                    Optional.ofNullable(ticket.getUsuarioCreador()).ifPresent(destinatarios::add); // Creador (Usuario)
                    destinatarios.addAll(getAdmins()); // Todos los admins
                }
                break;

            case PERFIL_MODIFICADO:
            case NUEVO_USUARIO_REGISTRADO:
                if (usuarioData != null) {
                    destinatarios.add(usuarioData); // El propio usuario afectado
                }
                if (evento.getTipo() == TipoNotificacion.NUEVO_USUARIO_REGISTRADO) {
                    destinatarios.addAll(getAdmins()); // Y los admins
                }
                break;

            case NUEVO_MENSAJE_EN_TICKET:
                // Lógica para comentarios (configurable)
                if (ticket != null) {
                    Optional.ofNullable(ticket.getUsuarioCreador()).ifPresent(destinatarios::add);
                    Optional.ofNullable(ticket.getUsuarioAsignado()).ifPresent(destinatarios::add);
                    destinatarios.addAll(getAdmins());
                }
                break;
        }
        return destinatarios;
    }
    public Set<Usuario> resolverNuevoMensaje(){

        return null;
    }

    private Set<Usuario> getAdmins() {
        return usuarioRepository.findByRoles_Nombre("ROLE_ADMIN");
    }
}
