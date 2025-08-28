package com.webserdi.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para encapsular los detalles de una revisión de auditoría.
 * Contiene la información de la revisión (quién, cuándo) y el estado
 * de la entidad en ese punto en el tiempo.
 * @param <T> El tipo del DTO de la entidad auditada (ej. TicketDto).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RevisionDetailsDto<T> {

    private int revisionNumber;
    private LocalDateTime revisionTimestamp;
    private String authorEmail;
    private T entitySnapshot;

}