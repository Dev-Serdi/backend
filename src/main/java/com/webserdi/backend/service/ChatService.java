package com.webserdi.backend.service;

import com.webserdi.backend.dto.ChatMessageCreateDto;
import com.webserdi.backend.dto.ChatMessageDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

public interface ChatService {

    /**
     * Retrieves messages for a specific ticket's chat, paginated.
     * @param ticketId The ID of the ticket.
     * @param pageable Pagination information.
     * @return A page of ChatMessageDto.
     */
    Page<ChatMessageDto> getMessagesByTicketId(Long ticketId, Pageable pageable);

    /**
     * Posts a new message (text or file) to a ticket's chat.
     * @param ticketId The ID of the ticket.
     * @param messageDto DTO containing the message content.
     * @param file Optional file attachment (PDF or image).
     * @param authentication The authentication object of the sender.
     * @return The created ChatMessageDto.
     */
    ChatMessageDto postMessage(Long ticketId, ChatMessageCreateDto messageDto, MultipartFile file, Authentication authentication);
    ChatMessageDto processMessage(String chatId, ChatMessageCreateDto message, Authentication authentication);
}