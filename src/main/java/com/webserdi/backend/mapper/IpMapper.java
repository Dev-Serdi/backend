package com.webserdi.backend.mapper;

import com.webserdi.backend.dto.IpDto;
import com.webserdi.backend.entity.Ip;
import com.webserdi.backend.entity.Usuario;
import org.springframework.stereotype.Component;

@Component
public class IpMapper {

    public static IpDto toDto(Ip ip) {
        if (ip == null) {
            return null;
        }
        IpDto dto = new IpDto();
        dto.setId(ip.getId());
        dto.setIp(ip.getIp());
        dto.setFechaRegistro(ip.getFechaRegistro());
        if (ip.getUsuario() != null) {
            dto.setUsuarioEmail(ip.getUsuario().getEmail());
            dto.setNombreUsuario(ip.getUsuario().getNombre() + " " + ip.getUsuario().getApellido());
        }
        return dto;
    }
}