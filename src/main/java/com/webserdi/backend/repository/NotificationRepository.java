package com.webserdi.backend.repository;

import com.webserdi.backend.dto.NotificationDto;
import com.webserdi.backend.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // Este es el método que construye los DTOs directamente usando una consulta de constructor JPQL.
    // Es la forma más eficiente y segura de evitar problemas de Lazy Loading.
    @Query("SELECT new com.webserdi.backend.dto.NotificationDto(" +
           "   n.id, n.title, n.body, n.url, n.timestamp, n.readed, n.user.id) " +
           "FROM Notification n WHERE n.user.id = :userId AND n.readed = false ORDER BY n.timestamp DESC ")
    List<NotificationDto> findNotificationsByUserIdOrderByTimestampDesc(@Param("userId") Long userId);
    Page<Notification> findAllByUserIdOrderByTimestampDesc(Long UserId, Pageable pageable);
    /**
     * Updates the 'readed' status of multiple notifications in a single query.
     * This is far more efficient than fetching and saving each entity individually.
     *
     * @param userId A list of notification IDs to mark as read.
     */
    @Modifying
    @Query("UPDATE Notification n SET n.readed = true WHERE n.user.id IN :userId")
    void markAsReadByIds(@Param("userId") Long userId);
}