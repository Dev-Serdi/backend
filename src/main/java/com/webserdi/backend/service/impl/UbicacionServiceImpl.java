package com.webserdi.backend.service.impl;

import com.webserdi.backend.dto.UbicacionDto;
import com.webserdi.backend.entity.Ubicacion;
import com.webserdi.backend.repository.UbicacionRepository;
import com.webserdi.backend.service.UbicacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UbicacionServiceImpl implements UbicacionService {
    private final UbicacionRepository ubicacionRepository;

    @Override
    public UbicacionDto createUbicacion(UbicacionDto dto) {
        Ubicacion ubicacion = new Ubicacion();
        Ubicacion ubicacionSaved;
        ubicacion.setNombre(dto.getNombre());
        ubicacionSaved = ubicacionRepository.save(ubicacion);
        return dto.getDto(ubicacionSaved);
    }

    @Override
    public List<UbicacionDto> getUbicaciones() {
        List<Ubicacion> ubicaciones = ubicacionRepository.findAll();
        return ubicaciones.stream()
                .map(ubicacion -> new UbicacionDto().getDto(ubicacion))
                .toList();
    }
}
