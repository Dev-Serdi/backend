package com.webserdi.backend.repository;

import com.webserdi.backend.entity.Carpeta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CarpetaRepository extends JpaRepository<Carpeta, Long> {
    List<Carpeta> findByCarpetaPadreIdAndUsuarioId(Long carpetaPadreId, Long usuarioId);

    List<Carpeta> findByCarpetaPadreIdIsNullAndUsuarioId(Long usuarioId);
    List<Carpeta> findByUsuarioIdAndActivoTrue(Long usuarioId);
    List<Carpeta> findByActivoFalseAndUsuarioId(Long usuarioId);

}
