package com.webserdi.backend.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
public class Publicacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario autor;

    @ManyToOne
    @JoinColumn(name = "sitio_id", nullable = false)
    private Sitio sitio;

    @Column(columnDefinition = "TEXT")
    private String contenido;

    private String nombreArchivo;
    private String tipoArchivo;

    @CreationTimestamp
    private LocalDateTime fechaPublicacion;
}
