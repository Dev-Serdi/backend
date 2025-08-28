package com.webserdi.backend.service;

import com.webserdi.backend.dto.PublicacionDto;

import java.util.List;

public interface PublicacionService {
    List<PublicacionDto> obtenerPorSitio(Long sitioId);
    PublicacionDto crearPublicacion(PublicacionDto dto);
}

