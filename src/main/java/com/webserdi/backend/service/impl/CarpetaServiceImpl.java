package com.webserdi.backend.service.impl;

import com.webserdi.backend.dto.CarpetaDto;
import com.webserdi.backend.entity.Carpeta;
import com.webserdi.backend.entity.Usuario;
import com.webserdi.backend.exception.ResourceNotFoundException;
import com.webserdi.backend.mapper.CarpetaMapper;
import com.webserdi.backend.repository.CarpetaRepository;
import com.webserdi.backend.repository.UsuarioRepository;
import com.webserdi.backend.service.CarpetaService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
public class CarpetaServiceImpl implements CarpetaService {

    private final CarpetaRepository carpetaRepository;
    private final CarpetaMapper carpetaMapper;
    private final UsuarioRepository usuarioRepository;

    @Override
    public CarpetaDto createCarpeta(CarpetaDto carpetaDto, Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        Carpeta carpeta = carpetaMapper.toEntity(carpetaDto);
        carpeta.setUsuario(usuario); // 👈 Aquí asignas el usuario

        if (carpetaDto.getCarpetaPadreId() != null) {
            Carpeta padre = carpetaRepository.findById(carpetaDto.getCarpetaPadreId())
                    .orElseThrow(() -> new ResourceNotFoundException("Carpeta padre no encontrada"));
            carpeta.setCarpetaPadre(padre);
        }
        ZonedDateTime utcMinus7Time = ZonedDateTime.now(ZoneOffset.UTC)
                .withZoneSameInstant(ZoneOffset.ofHours(-7));
        LocalDateTime now = utcMinus7Time.toLocalDateTime();
        carpeta.setFechaCreacion(now);

        carpetaRepository.save(carpeta);
        return carpetaMapper.toDto(carpeta);
    }

    @Override
    public CarpetaDto getCarpetaById(Long carpetaId) {
        Carpeta carpeta = carpetaRepository.findById(carpetaId)
                .orElseThrow(() -> new ResourceNotFoundException("Carpeta no encontrada con ID:" + carpetaId));
        return carpetaMapper.toDto(carpeta);
    }

    @Override
    public List<CarpetaDto> getAllCarpetas() {
        return carpetaRepository.findAll().stream()
                .map(carpetaMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public CarpetaDto updateCarpeta(Long carpetaId, CarpetaDto carpetaDto) {
        Carpeta carpeta = carpetaRepository.findById(carpetaId)
                .orElseThrow(() -> new ResourceNotFoundException("Carpeta no encontrada con id" + carpetaId));
        carpeta.setNombre(carpetaDto.getNombre());
        carpeta = carpetaRepository.save(carpeta);
        return carpetaMapper.toDto(carpeta);
    }

    @Override
    public void deleteCarpeta(Long carpetaId) {
        Carpeta carpeta = carpetaRepository.findById(carpetaId)
                .orElseThrow(() -> new ResourceNotFoundException("Carpeta no encontrada con id " + carpetaId));
        carpeta.setActivo(false); // ✅ Cambia el estado
        carpetaRepository.save(carpeta); // ✅ Guarda el cambio
    }

    @Override
    public List<CarpetaDto> getCarpetasByUsuario(Long usuarioId) {
        List<Carpeta> carpetas = carpetaRepository.findByUsuarioIdAndActivoTrue(usuarioId);
        return carpetas.stream()
                .map(carpetaMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CarpetaDto> getCarpetasEliminadasPorUsuario(Long usuarioId) {
        return carpetaRepository.findByActivoFalseAndUsuarioId(usuarioId)
                .stream()
                .map(carpetaMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public CarpetaDto restaurarCarpeta(Long carpetaId) {
        Carpeta carpeta = carpetaRepository.findById(carpetaId)
                .orElseThrow(() -> new RuntimeException("Carpeta no encontrada"));
        carpeta.setActivo(true);
        return carpetaMapper.toDto(carpetaRepository.save(carpeta));
    }
}