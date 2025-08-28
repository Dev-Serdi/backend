package com.webserdi.backend.controller;

import com.webserdi.backend.dto.ChatMessageCreateDto; // Usar tu DTO para crear mensajes
import com.webserdi.backend.dto.ChatMessageDto;       // Usar tu DTO para enviar mensajes
import com.webserdi.backend.service.ChatService;      // Usar tu servicio de Chat
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.security.core.Authentication; // Para obtener el usuario autenticado
import org.springframework.stereotype.Controller;
// import org.springframework.web.bind.annotation.CrossOrigin; // CrossOrigin no es usual en controladores STOMP, se configura en WebSocketConfig

/**
 * Controlador para manejar la comunicación de chat en tiempo real vía WebSockets (STOMP).
 */
@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private static final Logger logger = LoggerFactory.getLogger(ChatWebSocketController.class);
    private final ChatService chatService; 

    /**
     * Maneja el envío de un nuevo mensaje a un chat específico (asociado a un Ticket).
     * El cliente envía un mensaje a "/app/chat/{chatId}/sendMessage".
     * El mensaje procesado se envía a todos los suscriptores de "/ticket/chat/{chatId}".
     *
     * @param chatId El ID del Chat (que está asociado a un Ticket).
     * @param messageCreateDto DTO con el contenido del mensaje.
     * @param headerAccessor Accesor a las cabeceras del mensaje STOMP, útil para obtener la autenticación.
     * @return El {@link ChatMessageDto} del mensaje guardado y procesado.
     * @throws Exception Si ocurre un error durante el procesamiento.
     */
    @MessageMapping("/api/chat/{chatId}/sendmessage") // Endpoint al que los clientes envían mensajes
    @SendTo("/ticket/chat/{chatId}")             // Topic al que se suscribe el cliente para recibir mensajes
    public ChatMessageDto sendMessage(
            @DestinationVariable String chatId, // El ID del Chat (NO el ID del Ticket directamente)
            @Payload ChatMessageCreateDto messageCreateDto,
            SimpMessageHeaderAccessor headerAccessor) throws Exception { // SimpMessageHeaderAccessor para obtener el principal

        Authentication authentication = (Authentication) headerAccessor.getUser();
        String email = authentication.getName();

        if (!authentication.isAuthenticated()) {
            logger.error("Intento de enviar mensaje sin autenticación al chat ID: {}", chatId);
            // Considerar lanzar una excepción específica o devolver un error de alguna forma
            // STOMP no maneja bien las respuestas de error HTTP directamente como REST.
            // Podrías enviar un mensaje de error a un topic de error específico del usuario.
            throw new SecurityException("Usuario no autenticado. No se puede enviar el mensaje.");
        }

        logger.info("Mensaje recibido para chat ID {}: {} de usuario {}",
                chatId, messageCreateDto.getContent(), authentication.getName());

        Long chatIdentifier;
        try {
            chatIdentifier = Long.parseLong(chatId);
        } catch (NumberFormatException e) {
            logger.error("El ID del chat '{}' no es un número válido.", chatId);
            throw new IllegalArgumentException("El ID del chat debe ser un número.");
        }

        // Usar el método existente en ChatServiceImpl que ya maneja la lógica de guardado
        // y que (idealmente) ya está preparado para ser llamado desde un contexto no-HTTP.
        // El método postMessage en ChatServiceImpl espera un ticketId, no un chatId.
        // Necesitamos un método en ChatService que acepte chatId o adaptar el existente.

        // --- OPCIÓN 1: Adaptar ChatService para tomar chatId (preferido si es posible) ---
        // ChatMessageDto processedMessage = chatService.postMessageToChat(chatIdentifier, messageCreateDto, authentication);

        // --- OPCIÓN 2: Si ChatService.postMessage espera ticketId, necesitamos obtener el ticketId desde el chatId ---
        // Esto requeriría una consulta adicional o que el frontend envíe ticketId.
        // Por ahora, asumiremos que el `chatId` que llega aquí ES el ID de la entidad `Chat`.
        // Y que `ChatService` tiene un método que puede trabajar con `chatId` o que `postMessage`
        // puede ser adaptado.

        // El método `processMessage` que tenías en tu `ChatServiceImpl` parece un buen candidato
        // para ser llamado aquí, ya que toma `chatId` (como String) y `Authentication`.
        // Asegúrate que `processMessage` en `ChatServiceImpl` haga lo siguiente:
        // 1. Encuentre la entidad Chat por `chatId`.
        // 2. Cree y guarde el `ChatMessage` asociándolo a ese `Chat` y al `sender`.
        // 3. Devuelva el `ChatMessageDto`.
        // ¡IMPORTANTE! El método `processMessage` que proporcionaste en el contexto
        // usa `ChatMessage.builder()` y `ChatMessageDto.builder()` lo cual está bien,
        // pero el `ChatMessageDto.builder()` que mostraste no incluye todos los campos
        // que `ChatMessageMapper` sí incluye (como messageType, attachment info).
        // Es mejor reutilizar `ChatMessageMapper` para consistencia.

        // Llamando al método `processMessage` de tu `ChatServiceImpl` (adaptado)
        ChatMessageDto savedMessageDto = chatService.processMessage(chatId, messageCreateDto, authentication);

        logger.info("Mensaje procesado y enviado al topic /ticket/chat/{}: {}", chatId, savedMessageDto);
        return savedMessageDto; // Este DTO se enviará a los suscriptores
    }

    // Podrías añadir más mappings aquí, por ejemplo, para notificar "usuario está escribiendo"
    // @MessageMapping("/chat/{chatId}/typing")
    // @SendTo("/ticket/chat/{chatId}/typingStatus")
    // public TypingStatus handleTyping(@DestinationVariable String chatId, @Payload TypingUserDto userDto) {
    //     logger.info("Usuario {} está escribiendo en chat {}", userDto.getUsername(), chatId);
    //     return new TypingStatus(userDto.getUsername(), true);
    // }
}