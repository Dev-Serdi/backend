package com.webserdi.backend.repository;

import com.webserdi.backend.entity.Modulo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ModuloRepository extends JpaRepository<Modulo, Long> {
}