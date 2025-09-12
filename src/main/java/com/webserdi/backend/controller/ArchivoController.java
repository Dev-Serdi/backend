package com.webserdi.backend.controller;

import com.webserdi.backend.dto.ArchivoDto;
import com.webserdi.backend.entity.Archivo;
import com.webserdi.backend.exception.ResourceNotFoundException;
import com.webserdi.backend.service.ArchivoService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.webserdi.backend.repository.ArchivoRepository;


import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Configuration
@RestController
@RequestMapping("/api/archivos")
@AllArgsConstructor
public class ArchivoController {

    private final ArchivoService archivoService;
    private final ArchivoRepository archivoRepository;
    // Guardar metadatos del archivo
    @PostMapping
    public ResponseEntity<ArchivoDto> createArchivo(@RequestBody ArchivoDto archivoDto) {
        ArchivoDto createdArchivo = archivoService.createArchivo(archivoDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdArchivo);
    }

    // Subir archivo físico + metadatos
    @PostMapping("/uploads")
    public ResponseEntity<ArchivoDto> subirArchivoConContenido(
            @RequestParam("archivo") MultipartFile archivo,
            @RequestParam(value = "carpetaId", required = false) Long carpetaId,
            @RequestParam("usuarioId") Long usuarioId,
            @RequestParam(value = "sitioId", required = false) Long sitioId) {

        ArchivoDto archivoDto = archivoService.guardarArchivoConContenido(archivo, carpetaId, usuarioId, sitioId);
        return new ResponseEntity<>(archivoDto, HttpStatus.CREATED);
    }


    @GetMapping("/{archivoId}")
    public ResponseEntity<ArchivoDto> getArchivoById(@PathVariable Long archivoId) {
        ArchivoDto archivoDto = archivoService.getArchivoById(archivoId);
        return ResponseEntity.ok(archivoDto);
    }
    @GetMapping("/carpeta/{carpetaId}")
    public ResponseEntity<List<ArchivoDto>> listarArchivosPorCarpeta(
            @PathVariable Long carpetaId,
            @RequestParam("usuarioId") Long usuarioId) {

        List<ArchivoDto> archivos = archivoService.getArchivosPorCarpeta(carpetaId, usuarioId);
        return ResponseEntity.ok(archivos);
    }

    @GetMapping
    public ResponseEntity<List<ArchivoDto>> getAllArchivos() {
        List<ArchivoDto> archivos = archivoService.getAllArchivos();
        return ResponseEntity.ok(archivos);
    }


    @PutMapping("/{archivoId}")
    public ResponseEntity<ArchivoDto> updateArchivo(@PathVariable Long archivoId, @RequestBody ArchivoDto archivoDto) {
        ArchivoDto updatedArchivo = archivoService.updateArchivo(archivoId, archivoDto);
        return ResponseEntity.ok(updatedArchivo);
    }

    @DeleteMapping("/{archivoId}")
    public ResponseEntity<Void> deleteArchivo(@PathVariable Long archivoId) {
        archivoService.deleteArchivo(archivoId);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/sin-carpeta")
    public ResponseEntity<List<ArchivoDto>> listarArchivosSinCarpeta(
            @RequestParam("usuarioId") Long usuarioId) {
        List<ArchivoDto> archivos = archivoService.getArchivosPorCarpeta(null, usuarioId);
        return ResponseEntity.ok(archivos);
    }

    @PutMapping("/desactivar/{archivoId}")
    public ResponseEntity<Void> desactivarArchivo(@PathVariable Long archivoId) {
        archivoService.desactivarArchivo(archivoId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/ver/{id}")
    public ResponseEntity<Resource> verArchivo(@PathVariable Long id) {
        Archivo archivo = archivoRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Archivo no encontrado"));
        // Usar el campo 'ruta' como nombre del blob para descargar
        Resource recurso = azureBlobStorageServiceImpl.loadFileAsResource(archivo.getRuta());
        String contentType = archivo.getTipo();
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + archivo.getNombre() + "\"")
                .body(recurso);
    }

    @GetMapping("/sitio/{sitioId}")
    public ResponseEntity<List<ArchivoDto>> getArchivosPorSitio(@PathVariable Long sitioId) {
        List<ArchivoDto> archivos = archivoService.getArchivosPorSitio(sitioId);
        return ResponseEntity.ok(archivos);
    }

    @GetMapping("/eliminados")
    public ResponseEntity<List<ArchivoDto>> getArchivosEliminados(@RequestParam Long usuarioId) {
        return ResponseEntity.ok(archivoService.getArchivosEliminadosPorUsuario(usuarioId));
    }

    @PutMapping("/restaurar/{id}")
    public ResponseEntity<ArchivoDto> restaurarArchivo(@PathVariable Long id) {
        return ResponseEntity.ok(archivoService.restaurarArchivo(id));
    }



}
