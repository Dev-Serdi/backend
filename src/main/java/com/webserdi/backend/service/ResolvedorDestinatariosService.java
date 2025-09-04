package com.webserdi.backend.service;

import com.webserdi.backend.entity.Usuario;
import com.webserdi.backend.service.impl.EventoNotificacionServiceImpl;
import java.util.Set;

public interface ResolvedorDestinatariosService {

    Set<Usuario> resolver(EventoNotificacionServiceImpl evento);

}
