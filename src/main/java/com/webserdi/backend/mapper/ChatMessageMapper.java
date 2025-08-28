package com.webserdi.backend.mapper;

import com.webserdi.backend.dto.ChatMessageDto;
import com.webserdi.backend.dto.UsuarioSimpleDto;
import com.webserdi.backend.entity.Chat;
import com.webserdi.backend.entity.ChatMessage;
import com.webserdi.backend.entity.Usuario;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ChatMessageMapper {
    public ChatMessageDto toDto(ChatMessage message) {
        if (message == null) {
            return null;
        }

        return ChatMessageDto.builder()
            .id(message.getId())
            .chatId(Optional.ofNullable(message.getChat())
                .map(Chat::getId)
                .orElse(null))
            .content(message.getContent())
            .messageType(message.getMessageType())
            .attachmentUrl(message.getAttachmentUrl())
            .attachmentFilename(message.getAttachmentFilename())
            .attachmentMimeType(message.getAttachmentMimeType())
            .timestamp(message.getTimestamp())
            .sender(Optional.ofNullable(message.getSender())
                .map(sender -> new UsuarioSimpleDto(
                    sender.getId(),
                    String.format("%s %s",
                        Optional.ofNullable(sender.getNombre()).orElse(""),
                        Optional.ofNullable(sender.getApellido()).orElse("")).trim()
                ))
                .orElse(null))
            .build();
    }
}