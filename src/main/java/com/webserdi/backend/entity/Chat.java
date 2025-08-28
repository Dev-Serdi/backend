package com.webserdi.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Link back to the Ticket (owning side is Ticket)
    @OneToOne(mappedBy = "chat", fetch = FetchType.LAZY)
    private Ticket ticket;

    // List of messages in this chat
    // CascadeType.ALL: If chat is deleted, messages are deleted.
    // orphanRemoval=true: If a message is removed from this list, it's deleted from DB.
    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("timestamp ASC") // Always fetch messages in chronological order
    private List<ChatMessage> messages = new ArrayList<>();

    // Helper method to add messages bidirectionally
    public void addMessage(ChatMessage message) {
        messages.add(message);
        message.setChat(this);
    }
}