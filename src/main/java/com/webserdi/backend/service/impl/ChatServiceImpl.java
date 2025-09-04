package com.webserdi.backend.service.impl;

import com.webserdi.backend.dto.ChatMessageCreateDto;
import com.webserdi.backend.dto.ChatMessageDto;
import com.webserdi.backend.entity.*;
import com.webserdi.backend.exception.ResourceNotFoundException;
import com.webserdi.backend.mapper.ChatMessageMapper;
import com.webserdi.backend.repository.ChatRepository;
import com.webserdi.backend.repository.ChatMessageRepository;
import com.webserdi.backend.repository.TicketRepository;
import com.webserdi.backend.repository.UsuarioRepository;
import com.webserdi.backend.service.ChatService;
import com.webserdi.backend.service.FileStorageService;
import com.webserdi.backend.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MimeTypeUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private static final Logger logger = LoggerFactory.getLogger(ChatServiceImpl.class);
    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList(MimeTypeUtils.IMAGE_JPEG_VALUE, MimeTypeUtils.IMAGE_PNG_VALUE, MimeTypeUtils.IMAGE_GIF_VALUE);
    private static final String ALLOWED_PDF_TYPE = "application/pdf";
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB limit

    private final TicketRepository ticketRepository;
    private final ChatRepository chatRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UsuarioRepository usuarioRepository;
    private final ChatMessageMapper chatMessageMapper;
    private final FileStorageService fileStorageService;
    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationService notificationService;
    private final GestorNotificacionesImpl gestorNotificaciones;

    @Override
    @Transactional(readOnly = true)
    public Page<ChatMessageDto> getMessagesByTicketId(Long ticketId, Pageable pageable) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket no encontrado con id: " + ticketId));

        if (ticket.getChat() == null) {
            logger.warn("Ticket con ID {} has no associated chat.", ticketId);
            return Page.empty(pageable);
        }
        Page<ChatMessage> messagesPage = chatMessageRepository.findByChatIdFetchingSender(ticket.getChat().getId(), pageable);
        return messagesPage.map(chatMessageMapper::toDto);
    }

    @Override
    @Transactional
    public ChatMessageDto postMessage(Long ticketId, ChatMessageCreateDto messageDto, MultipartFile file, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new SecurityException("Usuario no autenticado. No se puede enviar el mensaje.");
        }
        String userEmail = authentication.getName();
        Usuario sender = usuarioRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + userEmail));

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket no encontrado con id: " + ticketId));

        Chat chat = ticket.getChat();
        if (chat == null) {
            logger.error("CRÍTICO: El chat es nulo para el ticket ID: {}. Esto no debería ocurrir.", ticketId);
            throw new IllegalStateException("El chat asociado al ticket no existe.");
        }
        ChatMessage message = new ChatMessage();
        message.setChat(chat);
        message.setSender(sender);
        // Obtener la hora actual en UTC-7
        ZonedDateTime utcMinus7Time = ZonedDateTime.now(ZoneOffset.UTC)
                .withZoneSameInstant(ZoneOffset.ofHours(-7));
        LocalDateTime now = utcMinus7Time.toLocalDateTime();
        message.setTimestamp(now);
        boolean hasContent = messageDto != null && StringUtils.hasText(messageDto.getContent());
        boolean hasFile = file != null && !file.isEmpty();

        if (!hasContent && !hasFile) {
            throw new ResourceNotFoundException("El mensaje debe tener contenido o un archivo adjunto.");
        }
        if (!ticket.getIsAttended() && !sender.getId().equals(ticket.getUsuarioCreador().getId()))
        {
            ticket.setIsAttended(true);
            ticket.setFechaRespuesta(now);
            ticketRepository.save(ticket);
        }
        if (hasFile) {
            validateFile(file);
            String storedFilename = fileStorageService.storeFile(file);
            message.setAttachmentUrl("/api/files/" + storedFilename);
            message.setAttachmentFilename(StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename())));
            message.setAttachmentMimeType(file.getContentType());

            if (ALLOWED_IMAGE_TYPES.contains(file.getContentType())) {
                message.setMessageType(ChatMessage.MessageType.IMAGE);
            } else if (ALLOWED_PDF_TYPE.equals(file.getContentType())) {
                message.setMessageType(ChatMessage.MessageType.PDF);
            } else {
                logger.warn("Tipo de archivo no reconocido explícitamente: {}. Se guardará como adjunto.", file.getContentType());
            }
            if (hasContent) {
                message.setContent(messageDto.getContent().trim() + "\n(Archivo: " + message.getAttachmentFilename() + ")");
            } else {
                message.setContent("Archivo adjunto: " + message.getAttachmentFilename());
            }

        } else { // Solo mensaje de texto
            message.setContent(messageDto.getContent().trim());
            message.setMessageType(ChatMessage.MessageType.TEXT);
        }

        ChatMessage savedMessage = chatMessageRepository.save(message);
        logger.info("Mensaje (vía REST) guardado con ID {} en chat ID {} para ticket ID {}", savedMessage.getId(), chat.getId(), ticketId);

        ChatMessageDto messageDtoToSend = chatMessageMapper.toDto(savedMessage);

        String destination = "/ticket/chat/" + chat.getId();
        logger.info("Transmitiendo mensaje (desde REST) a STOMP topic: {}", destination);
        messagingTemplate.convertAndSend(destination, messageDtoToSend);

//        notificationService.sendTicketMessageNotification(
//        , savedMessage);
        gestorNotificaciones.dispatch(new EventoNotificacionServiceImpl(TipoNotificacion.NUEVO_MENSAJE_EN_TICKET, ticket),null);

        return messageDtoToSend;
    }

    @Override
    @Transactional
    public ChatMessageDto processMessage(String chatIdString, ChatMessageCreateDto messageDto, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new SecurityException("Usuario no autenticado. No se puede procesar el mensaje.");
        }
        String username = authentication.getName();
        Usuario sender = usuarioRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        Long chatId;
        try {
            chatId = Long.parseLong(chatIdString);
        } catch (NumberFormatException e) {
            logger.error("El ID del chat '{}' (recibido vía WebSocket) no es un número válido.", chatIdString);
            throw new ResourceNotFoundException("El ID del chat proporcionado no es válido.");
        }

        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat no encontrado con ID: " + chatId));

        if (messageDto == null || !StringUtils.hasText(messageDto.getContent())) {
            throw new ResourceNotFoundException("El contenido del mensaje no puede estar vacío para mensajes WebSocket.");
        }

        ChatMessage message = ChatMessage.builder()
                .content(messageDto.getContent().trim())
                .sender(sender)
                .chat(chat)
                .messageType(ChatMessage.MessageType.TEXT)
                .build();

        ChatMessage savedMessage = chatMessageRepository.save(message);
        logger.info("Mensaje (vía WebSocket) guardado con ID {} en chat ID {}", savedMessage.getId(), chatId);

        Ticket ticket = chat.getTicket();
        if (ticket != null) {
            // Notificar a los usuarios implicados
            notificationService.sendTicketMessageNotification(ticket, savedMessage);
        } else {
            logger.error("CRÍTICO: El ticket es nulo para el chat ID: {}. No se puede enviar notificación.", chatId);
        }

        return chatMessageMapper.toDto(savedMessage);
    }

    private void validateFile(MultipartFile file) {
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new ResourceNotFoundException("El archivo excede el tamaño máximo permitido de " + (MAX_FILE_SIZE / 1024 / 1024) + "MB.");
        }
        String contentType = file.getContentType();
        if (contentType == null || (!ALLOWED_IMAGE_TYPES.contains(contentType) && !ALLOWED_PDF_TYPE.equals(contentType))) {
            throw new ResourceNotFoundException("Tipo de archivo no permitido: " + contentType + ". Permitidos: JPG, PNG, GIF, PDF.");
        }
    }
}
