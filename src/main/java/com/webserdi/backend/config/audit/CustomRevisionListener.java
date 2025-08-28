package com.webserdi.backend.config.audit;

import com.webserdi.backend.entity.Usuario;
import com.webserdi.backend.security.CustomUserDetailsService; // Asumiremos que tienes o crearás esta clase
import org.hibernate.envers.RevisionListener;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomRevisionListener implements RevisionListener {

    @Override
    public void newRevision(Object revisionEntity) {
        CustomRevisionEntity customRevisionEntity = (CustomRevisionEntity) revisionEntity;

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication(); // Obtener la autenticación actual

        // Si no hay autenticación (ej. un proceso batch) o es anónimo, no hacemos nada.
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            return;
        }

        // El objeto 'principal' contiene los detalles del usuario que inició sesión.
        Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetails) {
            // Por convención de Spring Security, getUsername() a menudo devuelve el email.
            String email = ((UserDetails) principal).getUsername();
            customRevisionEntity.setEmail(email);

            // Para obtener el ID, necesitarías un UserDetails personalizado.
            // Si tu 'principal' es una instancia de tu propia clase que implementa UserDetails y tiene el ID, puedes hacer un cast.
        }
    }
}