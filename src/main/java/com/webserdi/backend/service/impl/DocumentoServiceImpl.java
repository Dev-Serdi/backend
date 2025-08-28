package com.webserdi.backend.service.impl;

import com.webserdi.backend.dto.DocumentoDto;
import com.webserdi.backend.entity.Documento;
import com.webserdi.backend.exception.ResourceNotFoundException;
import com.webserdi.backend.mapper.DocumentoMapper;
import com.webserdi.backend.repository.DocumentoRepository;
import com.webserdi.backend.service.DocumentoService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
public class DocumentoServiceImpl implements DocumentoService {

    private final DocumentoRepository documentoRepository;
    private final DocumentoMapper documentoMapper;

    @Override
    public DocumentoDto createDocumento(DocumentoDto documentoDto) {
        Documento documento = documentoMapper.toEntity(documentoDto);
        documento = documentoRepository.save(documento);
        return documentoMapper.toDto(documento);
    }

    @Override
    public DocumentoDto getDocumentoById(Long documentoId) {
        Documento documento = documentoRepository.findById(documentoId)
                .orElseThrow(() -> new ResourceNotFoundException("Documento not found with id: " + documentoId));
        return documentoMapper.toDto(documento);
    }

    @Override
    public List<DocumentoDto> getAllDocumentos() {
        return documentoRepository.findAll().stream()
                .map(documentoMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public DocumentoDto updateDocumento(Long documentoId, DocumentoDto documentoDto) {
        Documento documento = documentoRepository.findById(documentoId)
                .orElseThrow(() -> new ResourceNotFoundException("Documento not found with id: " + documentoId));
        documento.setNombre(documentoDto.getNombre());
        documento.setUrl(documentoDto.getUrl());
        documento = documentoRepository.save(documento);
        return documentoMapper.toDto(documento);
    }

    @Override
    public void deleteDocumento(Long documentoId) {
        Documento documento = documentoRepository.findById(documentoId)
                .orElseThrow(() -> new ResourceNotFoundException("Documento not found with id: " + documentoId));
        documentoRepository.delete(documento);
    }
}