package com.webserdi.backend.controller;

import com.webserdi.backend.dto.IpDto;
import com.webserdi.backend.entity.Ip;
import com.webserdi.backend.service.IpService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ip")
@AllArgsConstructor
public class IpController {
    private final IpService ipService;

    @PostMapping
    public ResponseEntity<IpDto> registrarIpUsuario(@RequestBody IpDto ipDto, Authentication authentication) {
        String ip = ipDto.getIp();
        String email = authentication.getName();
        if (!authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        IpDto createdIp = ipService.registrarIpUsuario(ip, email);
        return ResponseEntity.ok(createdIp);
    }

    @GetMapping("/{usuarioId}")
    public ResponseEntity<Page<IpDto>> ObtenerIpsPorUsuario(
            @PageableDefault(size = 8) Pageable pageable,
            @PathVariable Long usuarioId) {
        Page<IpDto> ipDto = ipService.obtenerIpsPorUsuario(pageable, usuarioId);
        return ResponseEntity.ok(ipDto);
    }

//    @GetMapping
//    public ResponseEntity<Page<IpDto>> getAllIps(
//            @PageableDefault(size = 8) @SortDefault.SortDefaults({
//                    @SortDefault(sort = "fechaRegistro", direction = Sort.Direction.DESC),
//            })Pageable pageable) {
//        Page<IpDto> ips = ipService.obtenerTodasLasIps(pageable);
//        return ResponseEntity.ok(ips);
//    }
    @GetMapping
    public ResponseEntity<Page<IpDto>> getIpsByParams(
            @PageableDefault(size = 8) @SortDefault.SortDefaults({
                    @SortDefault(sort = "fechaRegistro", direction = Sort.Direction.DESC)
            }) Pageable pageable,
            @RequestParam(required = false) String param) {
        Page<IpDto> ipDtos = ipService.getIpsByParams(pageable, param);
        return ResponseEntity.ok(ipDtos);
    }

    }

