    package com.webserdi.backend.entity;

import lombok.Getter;

@Getter
public enum TipoNotificacion {
    // --- Notificaciones Configurables por el Usuario ---
    NUEVO_MENSAJE_EN_TICKET("Nuevo Mensaje", "Se ha hecho un nuevo comentario.", true),
    NUEVO_TICKET_ASIGNADO("Ticket Nuevo Asignado", "Se te ha asignado un nuevo ticket.", true),
    CAMBIO_ESTADO_TICKET("Cambio de Estado de Ticket", "El estado de un ticket relevante ha cambiado.", true),
    TICKET_MODIFICADO("Ticket Modificado", "Un ticket relevante ha sido modificado.", true),
    REASIGNACION_USUARIO_TICKET("Reasignación de Agente", "El agente de un ticket ha sido cambiado.", true),
    REASIGNACION_DEPARTAMENTO_TICKET("Reasignación de Departamento", "El departamento de un ticket ha sido cambiado.", true),
    TICKET_NO_AUTORIZADO("Ticket No Autorizado", "Un ticket que creaste o gestionas no fue autorizado.", true),
    PERFIL_MODIFICADO("Perfil Modificado", "Tu perfil de usuario ha sido modificado.", true),
    NUEVO_TICKET_CREADO("Nuevo Ticket Creado", "Se ha creado un nuevo ticket en el sistema.", true),
    FECHA_COMPROMISO_ASIGNADA("Fecha de Compromiso", "Se ha asignado una fecha de compromiso a un ticket",true),
    // --- Notificaciones de Sistema (Basadas en Roles, no configurables) ---
    NUEVO_USUARIO_REGISTRADO("Nuevo Usuario Registrado", "Admin: Un nuevo usuario se ha registrado en el sistema.", false),//lis
    USUARIO_MODIFICADO("Usuario Modificado", "Admin: Se ha modificado un usuario.", false);

    private final String titulo;
    private final String descripcion;
    private final boolean configurable;

    TipoNotificacion(String titulo, String descripcion, boolean configurable) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.configurable = configurable;
    }
}