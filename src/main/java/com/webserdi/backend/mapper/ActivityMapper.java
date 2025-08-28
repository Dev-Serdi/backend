package com.webserdi.backend.mapper;

import com.webserdi.backend.dto.ActivityDto;
import com.webserdi.backend.entity.Activity;
import com.webserdi.backend.entity.Item; // Assuming Item entity is in this package and has a getName() method

import com.webserdi.backend.entity.Usuario;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ActivityMapper {

    /**
     * Converts an {@link ActivityDto} to an {@link Activity} entity.
     * Handles specific logic for fields like 'approvalPercentage', 'createdAt', and 'updatedAt'
     * similarly to the original ActivityDto.toEntity() method.
     * Related entities (Usuario, Item list) are expected to be set by the service layer.
     *
     * @param activityDto The DTO to convert.
     * @return The resulting Activity entity, or null if the input DTO is null.
     */
    public Activity toEntity(ActivityDto activityDto) {
        if (activityDto == null) {
            return null;
        }

        Activity activity = new Activity();
        activity.setId(activityDto.getId());
        activity.setName(activityDto.getName());
        activity.setType(activityDto.getType());
        activity.setDueDate(activityDto.getDueDate());
        activity.setPriority(activityDto.getPriority());
        activity.setDescription(activityDto.getDescription());
        activity.setSendNotifications(activityDto.getSendNotifications());
        activity.setStatus(activityDto.getStatus() != null ? activityDto.getStatus() : false);
        activity.setCompletedAt(activityDto.getCompletedAt());

        // Set approvalPercentage only if type is "workflow"
        if ("workflow".equals(activityDto.getType())) {
            activity.setApprovalPercentage(activityDto.getApprovalPercentage());
        } else {
            activity.setApprovalPercentage(null);
        }

        // Set updatedAt to the current time
        // Obtener la hora actual en UTC-7
        ZonedDateTime utcMinus7Time = ZonedDateTime.now(ZoneOffset.UTC)
                .withZoneSameInstant(ZoneOffset.ofHours(-7));
        LocalDateTime now = utcMinus7Time.toLocalDateTime();
        activity.setUpdatedAt(now);

        if (activityDto.getId() == null) {
            activity.setCreatedAt(now);
        }
        // Note: Mapping for 'usuarioCreador', 'usuarioAsignado' (as Usuario objects)
        // and 'items' (as List<Item>) is typically handled in the service layer.
        // The service would use IDs from ActivityDto (e.g., activityDto.getUsuarioCreadorId())
        // to fetch/create and set these relationships.

        return activity;
    }

    /**
     * Converts an {@link Activity} entity to an {@link ActivityDto}.
     *
     * @param activity The entity to convert.
     * @return The resulting ActivityDto, or null if the input entity is null.
     */
    public ActivityDto toDto(Activity activity) {
        if (activity == null) {
            return null;
        }

        ActivityDto activityDto = new ActivityDto();
        activityDto.setId(activity.getId());
        activityDto.setName(activity.getName());
        activityDto.setType(activity.getType());
        activityDto.setDueDate(activity.getDueDate());
        activityDto.setPriority(activity.getPriority());
        activityDto.setDescription(activity.getDescription());
        activityDto.setSendNotifications(activity.getSendNotifications());
        activityDto.setApprovalPercentage(activity.getApprovalPercentage());
        activityDto.setCreatedAt(activity.getCreatedAt());
        activityDto.setUpdatedAt(activity.getUpdatedAt());
        activityDto.setStatus(activity.getStatus());
        activityDto.setCompletedAt(activity.getCompletedAt());

        // Map Usuario IDs
        if (activity.getUsuarioCreador() != null) {
            activityDto.setUsuariosCreadores(activity.getUsuarioCreador().getId());
        }

        if (activity.getUsuarioAsignado() != null) {
            List<Long> asignados = activity.getUsuarioAsignado().stream()
                    .map(Usuario::getId)
                    .collect(Collectors.toList());
            activityDto.setUsuariosAsignados(asignados);
        }



        // Map List<Item> to List<String>
        // This assumes your Item entity has a method like getName() or similar
        // to get its string representation, similar to Permiso::getNombre in RolMapper.
        if (activity.getItems() != null) {
            activityDto.setItems(activity.getItems().stream()
                    .map(Item::getName) // Or item -> item.getSomeStringField(), or item.toString()
                    // Ensure Item.java has a getName() method or adapt this line.
                    .collect(Collectors.toList()));
        } else {
            activityDto.setItems(new ArrayList<>()); // Or set to null, depending on desired DTO structure
        }

        return activityDto; // Corrected: return the populated DTO
    }
}