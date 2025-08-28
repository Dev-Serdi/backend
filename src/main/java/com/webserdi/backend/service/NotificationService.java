package com.webserdi.backend.service;

import com.webserdi.backend.dto.NotificationDto;
import com.webserdi.backend.entity.ChatMessage;
import com.webserdi.backend.entity.Ticket;
import com.webserdi.backend.entity.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NotificationService {
    void sendTicketMessageNotification(Ticket ticket, ChatMessage message);
    void sendTicketCreationNotification(Ticket ticket);
    void sendTicketStatusChangedNotification (Ticket ticket);
    List<NotificationDto> getNotifications (Long id);
    Page<NotificationDto> getAllNotifications(Long id, Pageable pageable);
    void setNotificationReaded(Long notificationId);
    void sendUserCreatedNotification (Usuario usuario);
}
