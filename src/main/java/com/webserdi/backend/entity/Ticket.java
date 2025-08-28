package com.webserdi.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.hibernate.envers.Audited;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.hibernate.envers.RelationTargetAuditMode.NOT_AUDITED;

/**
 * Entidad que representa un Ticket de soporte en el sistema.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Audited(targetAuditMode = NOT_AUDITED)
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 1000) // Aumentar longitud si es necesario
    private String descripcion;

    @Column(nullable = false, length = 255)
    private String tema;

    /** Código único del ticket, generado automáticamente. Ejemplo: ATN-AA-0001 */
    @Column(nullable = false, unique = true)
    private String codigo;

    /** Indica si el ticket ha sido movido a la papelera (soft delete). */
    @Column(nullable = false)
    private Boolean isTrashed = false; // Valor por defecto

    @Column(nullable = false)
    private Boolean isAttended = false; // Valor por defecto

    @Column(nullable = false)
    private Boolean isAuthorized = true; // Valor por defecto

    @Column(name="fecha_creacion", updatable = false) // updatable = false para que no se modifique en actualizaciones
    private LocalDateTime fechaCreacion;

    @Column(name="fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @Column(name="fecha_cierre")
    private LocalDateTime fechaCierre;

    @Column(name="fecha_compromiso")
    private LocalDate fechaCompromiso;

    @Column(name="fecha_respuesta")
    private LocalDateTime fechaRespuesta;

    @Column(name="fecha_vencimiento")
    private LocalDate fechaVencimiento;

    // --- Relaciones ---
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_creador_id", nullable = false)
    private Usuario usuarioCreador;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_asignado_id") // Nullable, un ticket puede no estar asignado
    private Usuario usuarioAsignado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_cerrar") // Nullable, un ticket puede no estar asignado
    private Usuario usuarioCerrar;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "departamento_id", nullable = false)
    private Departamento departamento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fuente_id", nullable = false)
    private Fuente fuente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "incidencia_id", nullable = false)
    private Incidencia incidencia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prioridad_id", nullable = false)
    private Prioridad prioridad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estado_id", nullable = false)
    private Estado estado;

    /**
     * Relación uno a uno con la entidad Chat.
     * Cada ticket tiene un chat asociado. La cascada asegura que el chat se gestione
     * junto con el ticket (creación, eliminación).
     */
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = false, orphanRemoval = true)
    @JoinColumn(name = "chat_id", referencedColumnName = "id", unique = true)
    private Chat chat;

    @Column
    private String ubicacion;

    /**
     * Método de ciclo de vida JPA que asegura la existencia de una entidad Chat
     * antes de persistir un nuevo Ticket.
     */
    @PrePersist
    private void ensureChatExists() {
        if (this.chat == null) {
            this.chat = new Chat();
            // this.chat.setTicket(this); // No es necesario si Chat no tiene referencia bidireccional o si es manejado por JPA
        }
    }
}