package com.webserdi.backend.mapper;

import com.webserdi.backend.dto.DocumentoDto;
import com.webserdi.backend.entity.Documento;
import org.springframework.stereotype.Component;

@Component
public class DocumentoMapper {

    public Documento toEntity(DocumentoDto documentoDto) {
        Documento documento = new Documento();
        documento.setId(documentoDto.getId());
        documento.setNombre(documentoDto.getNombre());
        documento.setUrl(documentoDto.getUrl());
        return documento;
    }

    public DocumentoDto toDto(Documento documento) {
        DocumentoDto documentoDto = new DocumentoDto();
        documentoDto.setId(documento.getId());
        documentoDto.setNombre(documento.getNombre());
        documentoDto.setUrl(documento.getUrl());
        return documentoDto;
    }
}