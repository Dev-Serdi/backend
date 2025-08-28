package com.webserdi.backend.repository;

import com.webserdi.backend.dto.NotificationDto;
import com.webserdi.backend.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // Este es el método que construye los DTOs directamente usando una consulta de constructor JPQL.
    // Es la forma más eficiente y segura de evitar problemas de Lazy Loading.
    @Query("SELECT new com.webserdi.backend.dto.NotificationDto(" +
           "   n.id, n.title, n.body, n.url, n.timestamp, n.readed, n.user.id) " +
           "FROM Notification n WHERE n.user.id = :userId AND n.readed = false ORDER BY n.timestamp DESC ")
    List<NotificationDto> findNotificationsByUserId(@Param("userId") Long userId);
    Page<Notification> findAllByUserId(Long UserId, Pageable pageable);
}