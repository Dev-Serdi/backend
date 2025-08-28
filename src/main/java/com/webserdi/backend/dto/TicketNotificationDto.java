package com.webserdi.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketNotificationDto {
    private Long ticketId;
    private Long messageId;
    private String senderName;
    private String messageContent;
}
