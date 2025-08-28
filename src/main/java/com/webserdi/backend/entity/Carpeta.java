package com.webserdi.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "carpetas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Carpeta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @CreationTimestamp
    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(nullable = false)
    private Boolean activo = true;

    // Relación con la carpeta padre (auto-referencia)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carpeta_padre_id")
    private Carpeta carpetaPadre;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;


    // Relación con las subcarpetas (hijas)
    @OneToMany(mappedBy = "carpetaPadre", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Carpeta> subcarpetas = new ArrayList<>();

    // Relación con los archivos contenidos en esta carpeta
    @OneToMany(mappedBy = "carpeta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Archivo> archivos = new ArrayList<>();

    // Métodos de conveniencia para manejar las relaciones bidireccionales
    public void agregarSubcarpeta(Carpeta subcarpeta) {
        subcarpetas.add(subcarpeta);
        subcarpeta.setCarpetaPadre(this);
    }

    public void removerSubcarpeta(Carpeta subcarpeta) {
        subcarpetas.remove(subcarpeta);
        subcarpeta.setCarpetaPadre(null);
    }

    public void agregarArchivo(Archivo archivo) {
        archivos.add(archivo);
        archivo.setCarpeta(this);
    }

    public void removerArchivo(Archivo archivo) {
        archivos.remove(archivo);
        archivo.setCarpeta(null);
    }
}