package com.webserdi.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "sitio")
public class Sitio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(length = 500)
    private String descripcion;

    @Column(nullable = false, length = 50)
    private String tipo;

    @Column(nullable = false, length = 20)
    private String visibilidad;

    @Column(nullable = false, unique = true, length = 50)
    private String slug;

    @Column(name = "favorito")
    private Boolean favorito = false;

    @CreationTimestamp
    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @UpdateTimestamp
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @Column(nullable = false, columnDefinition = "bit default 1")
    private boolean activo = true;


    // ✅ Relación corregida con usuarios asignados al sitio
    @ManyToMany
    @JoinTable(
            name = "sitio_usuarios",
            joinColumns = @JoinColumn(name = "sitio_id"),
            inverseJoinColumns = @JoinColumn(name = "usuario_id")
    )
    private Set<Usuario> usuarios;

    @OneToMany(mappedBy = "sitio", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Archivo> archivos;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creador_id", nullable = false)
    private Usuario creador;

    @ManyToMany
    @JoinTable(
            name = "sitio_administradores",
            joinColumns = @JoinColumn(name = "sitio_id"),
            inverseJoinColumns = @JoinColumn(name = "usuario_id")
    )

    private Set<Usuario> administradores;
}
