package com.webserdi.backend.controller;
import com.webserdi.backend.dto.JwtAuthResponse;
import com.webserdi.backend.dto.LoginDto;
import com.webserdi.backend.dto.UsuarioDto;
import com.webserdi.backend.security.JwtTokenProvider;
import com.webserdi.backend.service.AuthService;
import com.webserdi.backend.service.UsuarioService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Collections;

@AllArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final UsuarioService usuarioService;
    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;

    // Build Login REST API
    @PreAuthorize("permitAll()")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDto loginDto) {
        try {
            String token = authService.login(loginDto);


            JwtAuthResponse jwtAuthResponse = new JwtAuthResponse();
            jwtAuthResponse.setAccessToken(token);

            return ResponseEntity.ok(jwtAuthResponse);
        } catch (DisabledException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Collections.singletonMap("mensaje", "La cuenta de usuario está deshabilitada."));
        } catch (AuthenticationException e) { // Captura todas las excepciones de autenticación
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("mensaje", "Usuario o contraseña incorrectos"));
        }
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            String token = extractTokenFromHeader(authorizationHeader);

            if (token != null) {
//                jwtTokenProvider.invalidateToken(token);
                // Limpiar el contexto de seguridad para el hilo actual
                SecurityContextHolder.clearContext();
                return ResponseEntity.ok(Collections.singletonMap("mensaje", "Logout exitoso"));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Collections.singletonMap("error", "El encabezado de autorización es inválido o no contiene un token Bearer."));
            }
        } catch (Exception e) {
            log.error("Error inesperado durante el logout: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Ocurrió un error inesperado durante el logout."));
        }
    }

    @PreAuthorize("permitAll()")
    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestBody UsuarioDto user) {
        try {
            UsuarioDto createdUser = usuarioService.createUsuario(user);
            return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
        } catch (DataIntegrityViolationException e) {
            log.warn("Intento de registro con un email que ya existe: {}", user.getEmail());
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(Collections.singletonMap("error", "El email proporcionado ya está en uso."));
        } catch (Exception e) {
            log.error("Error inesperado durante el registro del usuario: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "El correo ya se encuentra en uso."));
        }
    }
    private String extractTokenFromHeader(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        return null;
    }
}