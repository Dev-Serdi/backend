package com.webserdi.backend.controller;

import com.webserdi.backend.dto.ChatMessageCreateDto;
import com.webserdi.backend.dto.ChatMessageDto;
import com.webserdi.backend.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/tickets/{ticketId}/chat") // Nested under tickets
@RequiredArgsConstructor
public class
ChatController {
    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    @GetMapping("/messages")
    // Add appropriate authorization check - e.g., user must have read access to the ticket
    public Page<ChatMessageDto> getChatMessages(
            @PathVariable Long ticketId,
            @PageableDefault(size = 20) Pageable pageable) {
        return chatService.getMessagesByTicketId(ticketId, pageable);
    }

    @PostMapping(
            value = "/messages",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.CREATED)
    public ChatMessageDto postChatMessage(
            @PathVariable Long ticketId,
            @RequestPart(value = "message")
            @Validated ChatMessageCreateDto messageDto,
            @RequestPart(value = "file", required = false) MultipartFile file,
            Authentication authentication) {
        return chatService.postMessage(ticketId, messageDto, file, authentication);
    }

    @MessageMapping("/chat/{ticketId}")
    @SendTo("/topic/chat/{ticketId}")
    public ChatMessageDto handleMessage(@DestinationVariable Long ticketId,
                                        ChatMessageCreateDto message,
                                        Authentication authentication) {
        ChatMessageDto savedMessage = chatService.processMessage(ticketId.toString(), message, authentication);
        // Notificar a los usuarios suscritos al chat específico
        messagingTemplate.convertAndSend("/topic/chat/" + ticketId, savedMessage);
        System.out.println("Suscrito al chat" + ticketId);
        return savedMessage;
    }
}