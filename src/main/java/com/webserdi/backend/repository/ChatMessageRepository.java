package com.webserdi.backend.repository;

import com.webserdi.backend.entity.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    // Find messages by Chat ID, ordered by timestamp, fetch sender eagerly
    @Query("SELECT msg FROM ChatMessage msg JOIN FETCH msg.sender WHERE msg.chat.id = :chatId ORDER BY msg.timestamp ASC")
    Page<ChatMessage> findByChatIdFetchingSender(Long chatId, Pageable pageable);

    // Or using Spring Data query derivation with FetchType.EAGER on sender (less flexible)
    // Page<ChatMessage> findByChatIdOrderByTimestampAsc(Long chatId, Pageable pageable);
}