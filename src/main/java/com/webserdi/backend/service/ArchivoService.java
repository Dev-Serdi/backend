package com.webserdi.backend.service;

import com.webserdi.backend.dto.ArchivoDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ArchivoService {
    ArchivoDto createArchivo(ArchivoDto archivoDto);
    ArchivoDto getArchivoById(Long archivoId); // nombre consistente con la implementación
    List<ArchivoDto> getAllArchivos();         // no carpetas, sino archivos
    ArchivoDto updateArchivo(Long archivoId, ArchivoDto archivoDto);
    void deleteArchivo(Long archivoId);        // minúscula en el parámetro
    List<ArchivoDto> getArchivosPorCarpeta(Long carpetaId, Long usuarioId);
    ArchivoDto guardarArchivoConContenido(MultipartFile archivo, Long carpetaId, Long usuarioId, Long sitioId);
    void desactivarArchivo(Long archivoId);
    List<ArchivoDto> getArchivosPorSitio(Long sitioId);
    List<ArchivoDto> getArchivosEliminadosPorUsuario(Long usuarioId);
    ArchivoDto restaurarArchivo(Long archivoId);

}
