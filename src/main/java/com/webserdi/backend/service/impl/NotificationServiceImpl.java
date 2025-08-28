package com.webserdi.backend.service.impl;

import com.webserdi.backend.dto.NotificationDto;
import com.webserdi.backend.entity.*;
import com.webserdi.backend.repository.NotificationRepository;
import com.webserdi.backend.repository.UsuarioRepository;
import com.webserdi.backend.service.EmailService;
import com.webserdi.backend.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationServiceImpl.class);
    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationRepository notificationRepository;
    private final UsuarioRepository usuarioRepository;
    private final EmailService emailService;


    @Override
    @Transactional
    public void sendTicketCreationNotification(Ticket ticket) {
        // Notificar solo al usuario asignado. El creador ya sabe que lo creó.
        Optional.ofNullable(ticket.getUsuarioAsignado()).ifPresent(recipient -> {
            String title = "Nuevo ticket asignado: " + ticket.getCodigo();
            String body = "Creado por: " + getFullName(ticket.getUsuarioCreador());
            createAndPersistNotification(new HashSet<>(Collections.singletonList(recipient)), ticket, title, body,null);
            emailService.sendEmailToUser(ticket.getUsuarioAsignado().getEmail(), title, body);
            emailService.sendEmailToAdmins(title,body);
        });
    }

    @Override
    @Transactional
    public void sendTicketMessageNotification(Ticket ticket, ChatMessage message) {
        Set<Usuario> recipients = new HashSet<>();

        // Añadir al creador y al usuario asignado como posibles destinatarios
        Optional.ofNullable(ticket.getUsuarioCreador()).ifPresent(recipients::add);
        Optional.ofNullable(ticket.getUsuarioAsignado()).ifPresent(recipients::add);

        // Quien envía el mensaje no debe recibir una notificación de su propio mensaje.
        Optional.ofNullable(message.getSender()).ifPresent(recipients::remove);

        if (recipients.isEmpty()) {
            logger.info("No hay destinatarios para la notificación de nuevo mensaje en el ticket {}", ticket.getCodigo());
            return;
        }

        String title = "Nuevo comentario en ticket: " + ticket.getCodigo();
        String body = "De: " + getFullName(message.getSender());
        emailService.sendEmailToUser(ticket.getUsuarioAsignado().getEmail(), title, body);
        createAndPersistNotification(recipients, ticket, title, body,null);
    }

    @Override
    @Transactional
    public void sendTicketStatusChangedNotification(Ticket ticket) {
        Set<Usuario> recipients = new HashSet<>();

        // Notificar tanto al creador como al usuario asignado sobre el cambio de estado.
        Optional.ofNullable(ticket.getUsuarioCreador()).ifPresent(recipients::add);
        Optional.ofNullable(ticket.getUsuarioAsignado()).ifPresent(recipients::add);

        if (recipients.isEmpty()) {
            logger.info("No hay destinatarios para la notificación de cambio de estado en el ticket {}", ticket.getCodigo());
            return;
        }

        String title = "Estado del ticket actualizado: " + ticket.getCodigo();
        String body = "Se movió a: " + ticket.getEstado().getNombre();
        emailService.sendEmailToMultipleUsers(recipients, title, body);
        createAndPersistNotification(recipients, ticket, title, body,null);
    }
    private Set<Usuario> getAdministradores() {
        return usuarioRepository.findByRoles_Nombre("ROLE_ADMIN"); // Ajusta según tu lógica de roles
    }
    /**
     * Método centralizado para crear, persistir y enviar una notificación.
     * Esto asegura que una notificación siempre se guarde en la BD antes de ser enviada por WebSocket.
     *
     * @param recipients Los usuarios que recibirán la notificación.
     * @param ticket    El ticket relacionado con la notificación.
     * @param title     El título/mensaje principal de la notificación.
     * @param body      El mensaje/cuerpo secundario de la notificación.
     */
    private void createAndPersistNotification(Set<Usuario> recipients, Ticket ticket, String title, String body, String route) {
        recipients.addAll(getAdministradores());
        for (Usuario recipient : recipients) {
            if (recipient == null || recipient.getEmail() == null) {
                logger.warn("Se intentó enviar una notificación a un destinatario nulo o sin email");
                continue;
            }
            Notification notification = new Notification();
            notification.setUser(recipient);
            notification.setTitle(title);
            notification.setBody(body);
            // Verifica si ticket es null para saber si se envia informacion sobre un ticket o un usuario que se registro
            String url = (ticket != null) ? "/helpdesk/task/" + ticket.getId() : route;
            notification.setUrl(url);
            notification.setTimestamp(getCurrentTime());
            notification.setReaded(false);
            notificationRepository.save(notification);

            NotificationDto notificationDto = new NotificationDto(
                    notification.getId(),
                    notification.getTitle(),
                    notification.getBody(),
                    notification.getUrl(),
                    notification.getTimestamp(),
                    notification.isReaded(),
                    recipient.getId()
            );

            String destination = "/user/" + recipient.getEmail() + "/notifications";
            messagingTemplate.convertAndSend(destination, notificationDto);
        }
    }

    /**
     * Obtiene todas las notificaciones para un usuario específico, ordenadas por fecha descendente.
     * @param userId El ID del usuario.
     * @return Una lista de {@link NotificationDto}.
     */
    @Override
    @Transactional(readOnly = true)
    public List<NotificationDto> getNotifications(Long userId) {
        logger.info("Buscando notificaciones para el usuario con ID: {} usando una consulta de constructor JPQL.", userId);

        // Esta es la forma más eficiente y segura.
        // Llama directamente al método del repositorio que construye los DTOs.
        List<NotificationDto> notifications = notificationRepository.findNotificationsByUserId(userId);

        logger.info("Se encontraron y mapearon {} notificaciones para el usuario con ID: {}", notifications.size(), userId);

        return notifications;
    }

    @Override
    public Page<NotificationDto> getAllNotifications(Long userId, Pageable pageable) {
        Page<Notification> notifications = notificationRepository.findAllByUserId(userId, pageable);
        return notifications.map(NotificationDto::new);
    }

    @Override
    public void setNotificationReaded(Long notificationId) {
        Optional<Notification> notification = notificationRepository.findById(notificationId);
        notification.ifPresent(n -> {
            n.setReaded(true);
            notificationRepository.save(n);
        });
    }
    @Override
    public void sendUserCreatedNotification(Usuario usuario) {
        String title = "Nuevo usuario registrado";
        String body = "Se ha registrado: " + getFullName(usuario)+ ", Favor de revisar su información.";
        String route = "/admin/edit-user/" + usuario.getId();
        Set<Usuario> admins = new HashSet<>(getAdministradores());
        emailService.sendEmailToAdmins(title, body);
        createAndPersistNotification(admins, null, title, body, route);
    }

    // Helper para obtener el nombre completo de forma segura
    private String getFullName(Usuario user) {
        if (user == null) {
            return "Usuario desconocido";
        }
        // Añade un espacio entre nombre y apellido
        return user.getNombre() + " " + user.getApellido();
    }

    private LocalDateTime getCurrentTime() {
        // Se mantiene la lógica original para obtener la hora en UTC-7 de forma robusta
        return ZonedDateTime.now(ZoneOffset.UTC)
                .withZoneSameInstant(ZoneOffset.ofHours(-7))
                .toLocalDateTime();
    }
}
