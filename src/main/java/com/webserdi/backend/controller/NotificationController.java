package com.webserdi.backend.controller;

import com.webserdi.backend.dto.NotificationDto;
import com.webserdi.backend.dto.TicketNotificationDto;
import com.webserdi.backend.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Controller
public class NotificationController {

    private final NotificationService notificationService;

    @MessageMapping("/notifications")
    @SendToUser("/user/notifications")
    public TicketNotificationDto sendNotification(@Payload TicketNotificationDto notification, Principal principal) {
        // Este metodo es un marcador de posición para manejar las notificaciones entrantes si es necesario.
        // El propósito principal de este controlador es establecer la conexión WebSocket
        // para las notificaciones. El envío real se realiza a través de SimpMessagingTemplate.
        return notification;
    }

    @PutMapping("/api/notifications/seen/{notificationId}")
    public TicketNotificationDto setReadedNotification(@PathVariable Long notificationId){
        notificationService.setNotificationReaded(notificationId);
        return (TicketNotificationDto) ResponseEntity.ok();
    }

    @GetMapping("/api/notifications/unreaded/{userId}")
    public List<NotificationDto> getNotifications (@PathVariable Long userId) {
        List<NotificationDto> notification = notificationService.getNotifications(userId);
        return ResponseEntity.ok(notification).getBody();
    }
    @GetMapping("/api/notifications/all/{userId}")
    public Page<NotificationDto> getReadedNotifications (
            @PageableDefault(size = 8, sort = "timestamp") Pageable pageable,
            @PathVariable Long userId){
        Page<NotificationDto> notification = notificationService.getAllNotifications(userId, pageable);
        return ResponseEntity.ok(notification).getBody();
    }


}
