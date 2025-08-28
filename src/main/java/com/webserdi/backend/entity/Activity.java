package com.webserdi.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "activities")
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String type;
    private LocalDate dueDate;
    private String priority;
    private String description;
    private Boolean sendNotifications;
    private Integer approvalPercentage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;



    @OneToMany(mappedBy = "activity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Item> items = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_creador_id", nullable = false)
    private Usuario usuarioCreador;

    @Column(nullable = false, columnDefinition = "bit default 1")
    private boolean activo = true;

    @Column(nullable = false, columnDefinition = "bit default 0")
    private Boolean status = false;

    private LocalDateTime completedAt;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "activity_usuario_asignado",
            joinColumns = @JoinColumn(name = "activity_id"),
            inverseJoinColumns = @JoinColumn(name = "usuario_id")
    )
    private List<Usuario> usuarioAsignado;

}
