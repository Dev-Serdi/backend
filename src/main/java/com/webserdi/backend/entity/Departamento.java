package com.webserdi.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List; // Importar List
import com.fasterxml.jackson.annotation.JsonManagedReference; // Importante para evitar recursión infinita

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Departamento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String nombre;

    // --- CORRECCIÓN ---
    // Un departamento tiene muchas incidencias
    @OneToMany(mappedBy = "departamento", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JsonManagedReference // Gestiona la serialización de la lista de incidencias
    private List<Incidencia> incidencias;
}