package com.webserdi.backend.service;

import com.webserdi.backend.dto.DocumentoDto;

import java.util.List;

public interface DocumentoService {
    DocumentoDto createDocumento(DocumentoDto documentoDto);
    DocumentoDto getDocumentoById(Long documentoId);
    List<DocumentoDto> getAllDocumentos();
    DocumentoDto updateDocumento(Long documentoId, DocumentoDto documentoDto);
    void deleteDocumento(Long documentoId);
}