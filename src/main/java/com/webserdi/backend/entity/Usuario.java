package com.webserdi.backend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Entidad que representa un Usuario en el sistema.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
// Considerar usar @Getter, @Setter, @ToString individualmente y generar equals/hashCode con cuidado.
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    /**
     * Contraseña hasheada del usuario. Nullable si se permite creación de usuarios
     * sin contraseña inicial (ej. vía invitación o SSO).
     */
    @Column(nullable = false) // Puede ser true si la contraseña se establece después o es opcional
    private String password;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String apellido;
    @Column
    private String ubicacion;

    /** Indica si la cuenta del usuario está habilitada. */
    private boolean enabled = true; // Valor por defecto, podría ser false hasta activación

    /** Departamento al que pertenece el usuario (opcional). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "departamento_id") // Nullable si un usuario puede no tener departamento
    @JsonBackReference("usuario-departamento") // Nombre único para la referencia
    private Departamento departamento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "modulo_id") // Nullable si un usuario puede no tener departamento
    @JsonBackReference("usuario-modulo") // Nombre único para la referencia
    private Modulo modulo;

    /** Roles asignados al usuario. */
    @ManyToMany(fetch = FetchType.EAGER) // EAGER para roles es común si se usan en seguridad con frecuencia
    @JoinTable(name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id")
    )
    private Set<Rol> roles = new HashSet<>();

    /** Permisos directos asignados al usuario (adicionales a los de los roles). */
    @ManyToMany(fetch = FetchType.EAGER) // EAGER si se necesitan con frecuencia
    @JoinTable(
            name = "usuario_permisos",
            joinColumns = @JoinColumn(name = "usuario_id"),
            inverseJoinColumns = @JoinColumn(name = "permiso_id")
    )
    private Set<Permiso> permisos = new HashSet<>();

    @Column(name="fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @ElementCollection(fetch = FetchType.EAGER) // EAGER para tener las preferencias siempre disponibles
    @CollectionTable(name = "usuario_notificaciones_activas", joinColumns = @JoinColumn(name = "usuario_id"))
    @Enumerated(EnumType.STRING) // Guarda el nombre del enum ("NUEVO_TICKET_ASIGNADO") en la BD, no el índice numérico
    @Column(name = "tipo_notificacion")
    private Set<TipoNotificacion> notificacionesActivas = new HashSet<>();

}