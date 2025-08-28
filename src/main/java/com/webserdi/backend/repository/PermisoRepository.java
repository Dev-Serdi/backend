package com.webserdi.backend.repository;

import com.webserdi.backend.entity.Permiso;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;
import java.util.Optional;
import java.util.Set;

public interface PermisoRepository extends JpaRepository<Permiso,Long> {
    Optional<Permiso> findByNombre(String nombre);
    Set<Permiso> findByNombreIn(Set<String> nombres);
    Set<Permiso> findByModuloId(Long moduloId);

}
