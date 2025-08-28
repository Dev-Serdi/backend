package com.webserdi.backend.repository;

import com.webserdi.backend.entity.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
// Repositorio JPA para la entidad Activity (proporciona CRUD automático)
public interface ActivityRepository extends JpaRepository<Activity, Long> {


    // 👉 Obtiene las actividades que coinciden tanto por tipo como por prioridad
    List<Activity> findByTypeAndPriority(String type, String priority);
    // 👉 Obtiene las actividades que coinciden solo por tipo
    List<Activity> findByType(String type);
    // 👉 Obtiene las actividades que coinciden solo por prioridad
    List<Activity> findByPriority(String priority);
    // ✅ Nuevo método para actividades activas
    List<Activity> findByActivoTrue();
    List<Activity> findByActivoFalse();

}
