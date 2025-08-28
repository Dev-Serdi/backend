package com.webserdi.backend.controller;

import com.webserdi.backend.dto.DocumentoDto;
import com.webserdi.backend.service.DocumentoService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/documentos")
@AllArgsConstructor
public class DocumentoController {

    private final DocumentoService documentoService;

    @PostMapping
    public ResponseEntity<DocumentoDto> createDocumento(@RequestBody DocumentoDto documentoDto) {
        DocumentoDto createdDocumento = documentoService.createDocumento(documentoDto);
        return ResponseEntity.ok(createdDocumento);
    }

    @GetMapping("/{documentoId}")
    public ResponseEntity<DocumentoDto> getDocumentoById(@PathVariable Long documentoId) {
        DocumentoDto documentoDto = documentoService.getDocumentoById(documentoId);
        return ResponseEntity.ok(documentoDto);
    }

    @GetMapping
    public ResponseEntity<List<DocumentoDto>> getAllDocumentos() {
        List<DocumentoDto> documentos = documentoService.getAllDocumentos();
        return ResponseEntity.ok(documentos);
    }

    @PutMapping("/{documentoId}")
    public ResponseEntity<DocumentoDto> updateDocumento(@PathVariable Long documentoId, @RequestBody DocumentoDto documentoDto) {
        DocumentoDto updatedDocumento = documentoService.updateDocumento(documentoId, documentoDto);
        return ResponseEntity.ok(updatedDocumento);
    }

    @DeleteMapping("/{documentoId}")
    public ResponseEntity<Void> deleteDocumento(@PathVariable Long documentoId) {
        documentoService.deleteDocumento(documentoId);
        return ResponseEntity.noContent().build();
    }
}
