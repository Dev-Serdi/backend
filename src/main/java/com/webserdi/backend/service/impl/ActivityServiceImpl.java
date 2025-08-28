package com.webserdi.backend.service.impl;

import com.webserdi.backend.dto.ActivityDto;
import com.webserdi.backend.entity.Activity;
import com.webserdi.backend.entity.Item;
import com.webserdi.backend.entity.Usuario;
import com.webserdi.backend.mapper.ActivityMapper;
import com.webserdi.backend.repository.ActivityRepository;
import com.webserdi.backend.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ActivityServiceImpl {

    @Autowired
    private ActivityRepository activityRepo;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ActivityMapper activityMapper;

    public ActivityDto saveActivity(ActivityDto dto) {
        Activity activity = activityMapper.toEntity(dto);

        // Limpia items antes de agregar (importante para updates)
        activity.getItems().clear();

        // Obtiene y asigna el usuario creador (no puede ser null)
        Usuario creador = usuarioRepository.findById(dto.getUsuariosCreadores())
                .orElseThrow(() -> new RuntimeException("Usuario creador no encontrado"));
        activity.setUsuarioCreador(creador);

        // Limpia usuarios asignados antes de asignar nuevos
        activity.setUsuarioAsignado(null);

        // Agrega Items a la actividad
        for (String itemName : dto.getItems()) {
            Item item = new Item();
            item.setName(itemName);
            item.setActivity(activity);
            activity.getItems().add(item);
        }

        // Agrega usuarios asignados
        List<Usuario> asignados = usuarioRepository.findAllById(dto.getUsuariosAsignados());
        activity.setUsuarioAsignado(asignados);

        // Guarda la actividad (con cascade se guardan los items)
        return activityMapper.toDto(activityRepo.save(activity));
    }


    public List<ActivityDto> getAllActivities() {
        return activityRepo.findByActivoTrue().stream()
                .map(activityMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<ActivityDto> getDeletedActivities() {
        return activityRepo.findByActivoFalse().stream()
                .map(activityMapper::toDto)
                .collect(Collectors.toList());
    }

    public void restoreActivity(Long id) {
        Activity activity = activityRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Actividad no encontrada"));
        activity.setActivo(true);
        activityRepo.save(activity);
    }



    public Activity getActivityById(Long id) {
        return activityRepo.findById(id).orElse(null);
    }

    public void deleteActivity(Long id) {
        Activity activity = activityRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Actividad con ID " + id + " no encontrada."));

        activity.setActivo(false);
        activityRepo.save(activity);
    }


    public List<Activity> getFilteredActivities(String type, String priority) {
        if (type != null && priority != null) {
            return activityRepo.findByTypeAndPriority(type, priority);
        } else if (type != null) {
            return activityRepo.findByType(type);
        } else if (priority != null) {
            return activityRepo.findByPriority(priority);
        } else {
            return activityRepo.findAll();
        }
    }
}
