package com.webserdi.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonBackReference; // Importante para evitar recursión infinita

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Incidencia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String nombre;

    @ManyToOne(fetch = FetchType.LAZY) // LAZY es bueno para el rendimiento
    @JoinColumn(name = "departamento_id")
    @JsonBackReference // Evita que se serialice el departamento al serializar la incidencia
    private Departamento departamento;
}