package com.webserdi.backend.repository;

import com.webserdi.backend.entity.Ip;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IpRepository extends JpaRepository<Ip, Long> {
    // Métodos de búsqueda personalizados si los necesitas, por ejemplo:
    Page<Ip> findByUsuarioIdOrderByFechaRegistroDesc(Long usuarioId, Pageable pageable);

    Page<Ip> findByUsuario_NombreContainingOrUsuario_EmailContaining(
            String nombre, String email, Pageable pageable);
}