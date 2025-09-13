package com.webserdi.backend.repository;

import com.webserdi.backend.entity.Modulo;
import com.webserdi.backend.entity.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.Optional;
import java.util.Set;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByEmailAndIdNot(String email, long id);
    Page<Usuario> findAllByDepartamentoId(Long id, Pageable pageable);
    @Override
    @NonNull
    Page<Usuario> findAll(@NonNull Pageable pageable);

    Page<Usuario> findAllByNombreContainsIgnoreCaseOrApellidoContainsIgnoreCaseOrEmailContainsIgnoreCase(String searchTerm,String searchTerm2,String searchTerm3, Pageable pageable);

    Set<Usuario> findByRol_Nombre(String nombre);

    Page<Usuario> findAllByModuloId(Long moduloId, Pageable pageable);
}
