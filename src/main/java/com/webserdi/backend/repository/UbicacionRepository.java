package com.webserdi.backend.repository;
import com.webserdi.backend.entity.Ubicacion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UbicacionRepository extends JpaRepository<Ubicacion, Integer> {
    Ubicacion findByNombre(String nombre);
    Boolean existsByNombre(String nombre);
}