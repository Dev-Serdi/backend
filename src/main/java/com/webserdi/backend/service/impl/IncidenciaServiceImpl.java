package com.webserdi.backend.service.impl;

import com.webserdi.backend.dto.IncidenciaDto;
import com.webserdi.backend.entity.Departamento; // Importar la entidad Departamento
import com.webserdi.backend.entity.Incidencia;
import com.webserdi.backend.exception.ResourceNotFoundException;
import com.webserdi.backend.mapper.IncidenciaMapper;
import com.webserdi.backend.repository.DepartamentoRepository; // Importar DepartamentoRepository
import com.webserdi.backend.repository.IncidenciaRepository;
import com.webserdi.backend.service.IncidenciaService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor; // Asegúrate de que esta anotación incluya las nuevas dependencias o usa un constructor explícito
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor // Si usas esto, Lombok generará el constructor con todas las dependencias finales.
@Transactional
public class IncidenciaServiceImpl implements IncidenciaService {
    private final IncidenciaMapper incidenciaMapper;
    private final IncidenciaRepository incidenciaRepository;
    private final DepartamentoRepository departamentoRepository; // <-- Nueva dependencia
    // private final FuenteMapper fuenteMapper; // Si no se usa, se puede quitar

    @Override
    public IncidenciaDto createIncidencia(IncidenciaDto incidenciaDto) {
        Incidencia incidencia = new Incidencia();
        Incidencia savedIncidencia;
        // 1. Validar la entrada del DTO
        if (incidenciaDto == null) {
            throw new ResourceNotFoundException("El DTO de la incidencia no puede ser nulo.");
        }
        if (incidenciaDto.getNombre() == null || incidenciaDto.getNombre().isBlank()) {
            throw new ResourceNotFoundException("El nombre de la incidencia es obligatorio.");
        }
        if (incidenciaDto.getDepartamento() == null || incidenciaDto.getDepartamento().getId() == null) {
            incidencia.setNombre(incidenciaDto.getNombre().trim());
            savedIncidencia = incidenciaRepository.save(incidencia);
            return incidenciaMapper.toDto(savedIncidencia);
        }

        Long departamentoId = incidenciaDto.getDepartamento().getId();

        // 2. Buscar la entidad Departamento
        Departamento departamento = departamentoRepository.findById(departamentoId)
                .orElseThrow(() -> new ResourceNotFoundException("Departamento no encontrado con id: " + departamentoId));

        // 3. Crear y configurar la nueva entidad Incidencia
        //    No usamos incidenciaMapper.toEntity() aquí porque necesitamos establecer la entidad Departamento manualmente.
        incidencia.setNombre(incidenciaDto.getNombre().trim());
        incidencia.setDepartamento(departamento); // Asignar la entidad Departamento encontrada

        // 4. Guardar la nueva incidencia en la base de datos
        savedIncidencia = incidenciaRepository.save(incidencia);

        // 5. Mapear la entidad guardada (que ahora tiene un ID y el departamento asociado) de vuelta a DTO
        return incidenciaMapper.toDto(savedIncidencia);
    }

    @Override
    public IncidenciaDto getIncidenciaById(Long incidenciaId) {
        Incidencia incidencia = incidenciaRepository.findById(incidenciaId)
                .orElseThrow(() -> new ResourceNotFoundException("Incidencia no encontrada con id: " + incidenciaId));
        return incidenciaMapper.toDto(incidencia);
    }

    @Override
    public List<IncidenciaDto> getAllIncidencias() {
        List<Incidencia> incidencias = incidenciaRepository.findAll();
        // El mapeo a DTO ya incluye el departamento gracias a IncidenciaMapper
        return incidencias.stream()
                .map(incidenciaMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public IncidenciaDto updateIncidencia(Long incidenciaId, IncidenciaDto incidenciaDto) {
        Incidencia incidencia = incidenciaRepository.findById(incidenciaId)
                .orElseThrow(() -> new ResourceNotFoundException("Incidencia no encontrada con id: " + incidenciaId));

        if (incidenciaDto.getNombre() == null || incidenciaDto.getNombre().isBlank()) {
            throw new ResourceNotFoundException("El nombre de la incidencia no puede estar vacío para la actualización.");
        }
        incidencia.setNombre(incidenciaDto.getNombre().trim());

        // Opcional: Permitir cambiar el departamento de una incidencia
        if (incidenciaDto.getDepartamento() != null && incidenciaDto.getDepartamento().getId() != null) {
            Long nuevoDepartamentoId = incidenciaDto.getDepartamento().getId();
            // Solo actualiza si el departamento es diferente al actual
            if (!nuevoDepartamentoId.equals(incidencia.getDepartamento().getId())) {
                Departamento nuevoDepartamento = departamentoRepository.findById(nuevoDepartamentoId)
                        .orElseThrow(() -> new ResourceNotFoundException("Nuevo departamento no encontrado con id: " + nuevoDepartamentoId));
                incidencia.setDepartamento(nuevoDepartamento);
            }
        }
        // Si no se proporciona un nuevo departamento en el DTO, se mantiene el existente.

        Incidencia updatedIncidencia = incidenciaRepository.save(incidencia);
        return incidenciaMapper.toDto(updatedIncidencia);
    }

    @Override
    public void deleteIncidencia(Long incidenciaId) {
        Incidencia incidencia = incidenciaRepository.findById(incidenciaId)
                .orElseThrow(() -> new ResourceNotFoundException("Incidencia no encontrada con id: " + incidenciaId));
        incidenciaRepository.delete(incidencia);
    }
}