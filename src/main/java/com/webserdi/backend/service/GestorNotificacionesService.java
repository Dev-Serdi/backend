package com.webserdi.backend.service;


import com.webserdi.backend.entity.Usuario;
import com.webserdi.backend.service.impl.EventoNotificacionServiceImpl;

public interface GestorNotificacionesService {
    void dispatch(EventoNotificacionServiceImpl eventoNotificacionService, Usuario nuevoUsuarioAsignado);
}
