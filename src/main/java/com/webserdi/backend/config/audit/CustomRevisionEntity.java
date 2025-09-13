package com.webserdi.backend.config.audit;

import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.RevisionEntity;

@Data
@Entity
@RevisionEntity(CustomRevisionListener.class)
public class CustomRevisionEntity extends DefaultRevisionEntity {

    // Aquí guardaremos el ID del usuario que hizo el cambio.
    // Podrías guardar el email o el nombre si lo prefieres.
    private Long userId;
    private String email;

}