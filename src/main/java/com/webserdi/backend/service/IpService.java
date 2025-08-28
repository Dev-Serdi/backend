package com.webserdi.backend.service;

import com.webserdi.backend.dto.IpDto; // Asumiremos que crearás un DTO para Ip
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IpService {

    /**
     * Registra una nueva dirección IP para un usuario.
     *
     * @param Ip La dirección IP a registrar.
     * @param usuarioEmail   El Email del usuario asociado (puede ser null si es una IP genérica).
     * @return El DTO de la IP registrada.
     */
    IpDto registrarIpUsuario(String ip, String usuarioEmail);

    /**
     * Registra una nueva dirección IP sin asociarla directamente a un usuario en el momento del registro.
     *
     * @param Ip La dirección IP a registrar.
     * @param userAgent   La cadena User-Agent del cliente.
     * @return El DTO de la IP registrada.
     */


    /**
     * Obtiene todas las IPs registradas para un usuario específico.
     *
     * @param usuarioId El ID del usuario.
     * @return Una lista de DTOs de las IPs asociadas al usuario.
     */
    Page<IpDto> obtenerIpsPorUsuario(Pageable pageable, Long usuarioId);

    /**
     * Obtiene todas las IPs registradas en el sistema.
     * (Considera la paginación para este método si esperas muchas IPs)
     *
     * @return Una lista de todos los DTOs de IP.
     */
    Page<IpDto> obtenerTodasLasIps(Pageable pageable);
}