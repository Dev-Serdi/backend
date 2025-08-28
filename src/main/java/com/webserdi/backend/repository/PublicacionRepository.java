package com.webserdi.backend.repository;

import com.webserdi.backend.entity.Publicacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PublicacionRepository extends JpaRepository<Publicacion, Long> {
    List<Publicacion> findBySitioIdOrderByFechaPublicacionDesc(Long sitioId);
}

