package com.webserdi.backend.repository;

import com.webserdi.backend.entity.Rol;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface RolRepository extends JpaRepository<Rol, Integer> {
    Set<Rol> findByNombreIn(Set<String> nombres);
}
