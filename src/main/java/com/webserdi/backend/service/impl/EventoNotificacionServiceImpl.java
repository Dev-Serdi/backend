package com.webserdi.backend.service.impl;

import com.webserdi.backend.entity.TipoNotificacion;
import com.webserdi.backend.service.EventoNotificacionService;
import lombok.Getter;

@Getter
public class EventoNotificacionServiceImpl implements EventoNotificacionService {
    private final TipoNotificacion tipo;
    private final Object data; // Usamos Object para que sea flexible (puede ser un Ticket, un Mensaje, etc.)

    public EventoNotificacionServiceImpl(TipoNotificacion tipo, Object data) {
        this.tipo = tipo;
        this.data = data;
    }
}
