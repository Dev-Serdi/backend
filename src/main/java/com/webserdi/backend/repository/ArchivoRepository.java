package com.webserdi.backend.repository;

import com.webserdi.backend.entity.Archivo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArchivoRepository extends JpaRepository<Archivo, Long> {
    List<Archivo> findByCarpetaIdAndActivoTrue(Long carpetaId);
    List<Archivo> findByCarpetaIsNullAndActivoTrue();

    List<Archivo> findByCarpetaIdAndUsuarioIdAndActivoTrue(Long carpetaId, Long usuarioId);
    List<Archivo> findByCarpetaIsNullAndUsuarioIdAndActivoTrue(Long usuarioId);

    List<Archivo> findBySitioIdAndActivoTrue(Long sitioId);
    List<Archivo> findByActivoFalseAndUsuarioId(Long usuarioId);


}
