package com.webserdi.backend.service;

import com.webserdi.backend.dto.UbicacionDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UbicacionService {
    UbicacionDto createUbicacion(UbicacionDto dto);
    List<UbicacionDto> getUbicaciones();
}
