package com.webserdi.backend.dto;

import com.webserdi.backend.entity.Notification;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor // Este constructor es el que usará nuestra consulta JPQL
public class NotificationDto {
    private Long id;
    private String title;
    private String body;
    private String url;
    private LocalDateTime timestamp;
    private boolean readed;
    private Long userId;

    public NotificationDto(Notification notification) {
        this.id = notification.getId();
        this.title = notification.getTitle();
        this.body = notification.getBody();
        this.url = notification.getUrl();
        this.timestamp = notification.getTimestamp();
        this.readed = notification.isReaded();
        this.userId = notification.getUser() != null ? notification.getUser().getId() : null;
    }
}