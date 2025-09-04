package com.webserdi.backend.service;

import com.webserdi.backend.dto.NotificationDto;
import com.webserdi.backend.entity.ChatMessage;
import com.webserdi.backend.entity.Ticket;
import com.webserdi.backend.entity.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;

public interface NotificationService {
    void sendTicketMessageNotification(Ticket ticket, ChatMessage message);
//    void sendTicketCreationNotification(Ticket ticket);
//    void sendTicketStatusChangedNotification (Ticket ticket);
//    void sendUserCreatedNotification (Usuario usuario);
    void sendReassignedDepartment (Ticket ticket, Usuario usuario);
    List<NotificationDto> getNotifications (Long id);
    Page<NotificationDto> getAllNotifications(Long id, Pageable pageable);
    void setNotificationReaded(Long notificationId);
    void setBatchNotificationsReaded(Long userId);
    void sendNotification (Usuario recipient, Ticket ticket, String title, String body, String route);
}
