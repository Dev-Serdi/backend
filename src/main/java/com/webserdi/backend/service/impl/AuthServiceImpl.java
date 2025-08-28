package com.webserdi.backend.service.impl;

import com.webserdi.backend.dto.LoginDto;
import com.webserdi.backend.entity.Usuario;
import com.webserdi.backend.exception.ResourceNotFoundException;
import com.webserdi.backend.repository.UsuarioRepository;
import com.webserdi.backend.security.JwtTokenProvider;
import com.webserdi.backend.service.AuthService;
import com.webserdi.backend.service.EmailService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UsuarioRepository usuarioRepository;
    private final EmailService emailService;


    public AuthServiceImpl(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider, UsuarioRepository usuarioRepository, EmailService emailService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.usuarioRepository = usuarioRepository;
        this.emailService = emailService;
    }

    @Override
    public String login(LoginDto loginDto) {
        // Busca el usuario por email
        Usuario usuario = usuarioRepository.findByEmail(loginDto.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con el email: " + loginDto.getEmail())); // Lanza una excepción más específica

        // Verifica si el usuario está habilitado
        if (!usuario.isEnabled()) {
            // Si no está habilitado, lanza una excepción para prevenir el login
            throw new DisabledException("La cuenta de usuario está deshabilitada.");
        }

        // Si el usuario está habilitado, procede con la autenticación
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDto.getEmail(),
                        loginDto.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        return jwtTokenProvider.generateToken(authentication);
    }
}