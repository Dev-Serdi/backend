package com.webserdi.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String body;

    @Column(nullable = false)
    private String url; // URL a la que redirigir al hacer clic en la notificación

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column()
    private boolean readed; // Indica si la notificación ha sido leída

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Usuario user; // Usuario al que pertenece la notificación
}
