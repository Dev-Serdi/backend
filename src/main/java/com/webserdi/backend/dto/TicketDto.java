package com.webserdi.backend.dto;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class TicketDto {
    private Long id;
    private Long chatId;
    private String tema;
    private String descripcion;
    private String codigo;
    private Boolean isTrashed;
    private Boolean isAttended;
    private Boolean isAuthorized;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
    private LocalDateTime fechaCierre;
    private LocalDate fechaCompromiso;
    private LocalDateTime fechaRespuesta;
    private LocalDate fechaVencimiento;
    private Long usuarioCreador;
    private Long usuarioAsignado;
    private Long usuarioCerrar;
    private String usuarioCreadorNombres;
    private String usuarioAsignadoNombres;
    private String departamentoNombre;
    private String fuenteNombre;
    private String incidenciaNombre;
    private String prioridadNombre;
    private String estadoNombre;
    private Long departamento;
    private Long fuente;
    private Long incidencia;
    private Long prioridad;
    private Long estado;
    private String ubicacion;
}
