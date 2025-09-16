package com.webserdi.backend.service.impl;

import com.webserdi.backend.dto.UbicacionDto;
import com.webserdi.backend.entity.Ubicacion;
import com.webserdi.backend.repository.UbicacionRepository;
import com.webserdi.backend.service.UbicacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    @Override
    public UbicacionDto getUbicacionById(Long id) {
        Optional<Ubicacion> ubicacionOpt = ubicacionRepository.findById(id);
        return ubicacionOpt.map(u -> new UbicacionDto().getDto(u)).orElse(null);
    }

    @Override
    public UbicacionDto updateUbicacion(Long id, UbicacionDto dto) {
        Optional<Ubicacion> ubicacionOpt = ubicacionRepository.findById(id);
        if (ubicacionOpt.isEmpty()) {
            return null;
        }
        Ubicacion ubicacion = ubicacionOpt.get();
        ubicacion.setNombre(dto.getNombre());
        Ubicacion updated = ubicacionRepository.save(ubicacion);
        return dto.getDto(updated);
    }
}
