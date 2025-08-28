package com.webserdi.backend.service;

import com.webserdi.backend.dto.RolDto;
import java.util.List;

public interface RolService {
    RolDto createRol(RolDto rolDto);
    List<RolDto> getAllRoles();

}
