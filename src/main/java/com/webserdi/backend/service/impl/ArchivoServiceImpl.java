package com.webserdi.backend.service.impl;

import com.webserdi.backend.dto.ArchivoDto;
import com.webserdi.backend.entity.Archivo;
import com.webserdi.backend.entity.Carpeta;
import com.webserdi.backend.entity.Sitio;
import com.webserdi.backend.entity.Usuario;
import com.webserdi.backend.exception.ResourceNotFoundException;
import com.webserdi.backend.mapper.ArchivoMapper;
import com.webserdi.backend.repository.ArchivoRepository;
import com.webserdi.backend.repository.CarpetaRepository;
import com.webserdi.backend.repository.SitioRepository;
import com.webserdi.backend.repository.UsuarioRepository;
import com.webserdi.backend.service.ArchivoService;
import com.webserdi.backend.service.FileStorageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ArchivoServiceImpl implements ArchivoService {

    private final ArchivoRepository archivoRepository;
    private final ArchivoMapper archivoMapper;
    private final CarpetaRepository carpetaRepository;
    private final UsuarioRepository usuarioRepository;
    private final SitioRepository sitioRepository;
    private final FileStorageService fileStorageService;


    @Value("${file.upload-dir}")
    private String baseUploadDir;


    public ArchivoServiceImpl(ArchivoRepository archivoRepository,
                              ArchivoMapper archivoMapper,
                              CarpetaRepository carpetaRepository,
                              UsuarioRepository usuarioRepository,
                              SitioRepository sitioRepository,
                              FileStorageService fileStorageService) {
        this.archivoRepository = archivoRepository;
        this.archivoMapper = archivoMapper;
        this.carpetaRepository = carpetaRepository;
        this.usuarioRepository = usuarioRepository;
        this.sitioRepository = sitioRepository;
        this.fileStorageService = fileStorageService;
    }


    @Override
    public ArchivoDto createArchivo(ArchivoDto archivoDto) {
        if (archivoDto == null) {
            throw new ResourceNotFoundException("El DTO del archivo no puede ser nulo.");
        }
        if (archivoDto.getNombre() == null || archivoDto.getNombre().isBlank()) {
            throw new ResourceNotFoundException("El nombre del archivo es obligatorio.");
        }
        if (archivoDto.getCarpetaId() == null) {
            throw new ResourceNotFoundException("El ID de la carpeta es obligatorio para crear un archivo.");
        }

        Carpeta carpeta = carpetaRepository.findById(archivoDto.getCarpetaId())
                .orElseThrow(() -> new ResourceNotFoundException("Carpeta no encontrada con id: " + archivoDto.getCarpetaId()));

        Archivo archivo = archivoMapper.toEntity(archivoDto);
        archivo.setCarpeta(carpeta);
        archivo = archivoRepository.save(archivo);

        return archivoMapper.toDto(archivo);
    }

// Dentro de ArchivoServiceImpl

    @Override
    public ArchivoDto guardarArchivoConContenido(MultipartFile archivo, Long carpetaId, Long usuarioId, Long sitioId) {
        try {
            String nombreArchivo = archivo.getOriginalFilename();
            String tipoArchivo = archivo.getContentType();
            Long tamañoArchivo = archivo.getSize();

            // Subir archivo al blob y obtener el nombre único
            String uniqueFilename = fileStorageService.storeFile(archivo);
            // URL del blob (puedes construirla usando endpoint y contenedor si lo necesitas)
            String blobUrl = uniqueFilename;

            // Crear entidad
            Archivo entidad = new Archivo();
            entidad.setNombre(nombreArchivo);
            entidad.setTipo(tipoArchivo);
            entidad.setTamaño(tamañoArchivo);
            entidad.setRuta(blobUrl); // Guardar la referencia al blob

            Usuario usuario = usuarioRepository.findById(usuarioId)
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
            entidad.setUsuario(usuario);
            if (sitioId != null) {
                Sitio sitio = sitioRepository.findById(sitioId)
                        .orElseThrow(() -> new ResourceNotFoundException("Sitio no encontrado con id: " + sitioId));
                entidad.setSitio(sitio);
            }
            if (carpetaId != null) {
                Carpeta carpeta = carpetaRepository.findById(carpetaId)
                        .orElseThrow(() -> new ResourceNotFoundException("Carpeta no encontrada"));
                entidad.setCarpeta(carpeta);
            }
            archivoRepository.save(entidad);
            return archivoMapper.toDto(entidad);
        } catch (Exception e) {
            throw new RuntimeException("Error al guardar el archivo en blob: " + e.getMessage(), e);
        }
    }

    @Override
    public void desactivarArchivo(Long archivoId) {
        Archivo archivo = archivoRepository.findById(archivoId)
                .orElseThrow(() -> new ResourceNotFoundException("Archivo no encontrado"));

        archivo.setActivo(false);
        archivoRepository.save(archivo);
    }

    @Override
    public List<ArchivoDto> getArchivosPorSitio(Long sitioId) {
        List<Archivo> archivos = archivoRepository.findBySitioIdAndActivoTrue(sitioId);
        return archivos.stream()
                .map(archivoMapper::toDto)
                .collect(Collectors.toList());
    }




    @Override
    public List<ArchivoDto> getArchivosPorCarpeta(Long carpetaId, Long usuarioId) {
        List<Archivo> archivos;

        if (carpetaId == null) {
            archivos = archivoRepository.findByCarpetaIsNullAndUsuarioIdAndActivoTrue(usuarioId);
        } else {
            archivos = archivoRepository.findByCarpetaIdAndUsuarioIdAndActivoTrue(carpetaId, usuarioId);
        }

        return archivos.stream()
                .map(archivoMapper::toDto)
                .collect(Collectors.toList());
    }


    @Override
    public ArchivoDto getArchivoById(Long archivoId) {
        Archivo archivo = archivoRepository.findById(archivoId)
                .orElseThrow(() -> new ResourceNotFoundException("Archivo no encontrado con id: " + archivoId));
        return archivoMapper.toDto(archivo);
    }


    @Override
    public List<ArchivoDto> getAllArchivos() {
        return archivoRepository.findAll().stream()
                .map(archivoMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ArchivoDto updateArchivo(Long archivoId, ArchivoDto archivoDto) {
        Archivo archivo = archivoRepository.findById(archivoId)
                .orElseThrow(() -> new ResourceNotFoundException("Archivo no encontrado con id: " + archivoId));

        if (archivoDto.getNombre() == null || archivoDto.getNombre().isBlank()) {
            throw new ResourceNotFoundException("El nombre del archivo no puede estar vacío.");
        }

        archivo.setNombre(archivoDto.getNombre().trim());

        if (archivoDto.getCarpetaId() != null && !archivoDto.getCarpetaId().equals(archivo.getCarpeta().getId())) {
            Carpeta nuevaCarpeta = carpetaRepository.findById(archivoDto.getCarpetaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Carpeta no encontrada con id: " + archivoDto.getCarpetaId()));
            archivo.setCarpeta(nuevaCarpeta);
        }

        archivo = archivoRepository.save(archivo);
        return archivoMapper.toDto(archivo);
    }

    @Override
    public void deleteArchivo(Long archivoId) {
        Archivo archivo = archivoRepository.findById(archivoId)
                .orElseThrow(() -> new ResourceNotFoundException("Archivo no encontrado con id: " + archivoId));
        archivoRepository.delete(archivo);
    }

    @Override
    public List<ArchivoDto> getArchivosEliminadosPorUsuario(Long usuarioId) {
        return archivoRepository.findByActivoFalseAndUsuarioId(usuarioId)
                .stream()
                .map(archivoMapper::toDto)
                .collect(Collectors.toList());
    }


    @Override
    public ArchivoDto restaurarArchivo(Long archivoId) {
        Archivo archivo = archivoRepository.findById(archivoId)
                .orElseThrow(() -> new RuntimeException("Archivo no encontrado"));
        archivo.setActivo(true);
        return archivoMapper.toDto(archivoRepository.save(archivo));
    }



}
