package com.webserdi.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public final class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false) // A message must belong to a chat
    @JoinColumn(name = "chat_id", nullable = false)
    private Chat chat;

    @ManyToOne(fetch = FetchType.LAZY, optional = false) // A message must have a sender
    @JoinColumn(name = "sender_id", nullable = false)
    private Usuario sender;

    @Lob // Use Lob for potentially long text content
    @Column(columnDefinition = "TEXT")
    private String content; // Text content, can be null if it's just a file

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageType messageType = MessageType.TEXT; // Default to TEXT

    // --- Fields for Attachments ---
    @Column(name = "attachment_url")
    private String attachmentUrl; // URL or path to the stored file

    @Column(name = "attachment_filename")
    private String attachmentFilename; // Original filename

    @Column(name = "attachment_mime_type")
    private String attachmentMimeType; // e.g., "application/pdf", "image/jpeg"
    // --- End Attachment Fields ---

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime timestamp;

    public enum MessageType {
        TEXT,
        IMAGE,
        PDF
    }
}