package com.webserdi.backend.service.impl;
import com.webserdi.backend.entity.Usuario;
import com.webserdi.backend.repository.UsuarioRepository;
import com.webserdi.backend.service.EmailService;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class EmailServiceImpl implements EmailService {
    @Autowired
    private JavaMailSender mailSender;


    private SimpleMailMessage message = new SimpleMailMessage();

    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);
    @Value("${mail.sender}")
    private String sender;
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public void sendHtmlEmail(String to, String subject, String htmlBody) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            helper.setFrom(sender);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true); // 'true' indica que el cuerpo es HTML

            mailSender.send(mimeMessage);
            logger.info("Correo HTML enviado exitosamente a {}", to);

        } catch (Exception e) {
            logger.error("Error al enviar correo HTML a {}: {}", to, e.getMessage());
            // Considera lanzar una excepción personalizada aquí si es necesario
        }
    }

    @Override
    public void sendEmailToUser(String to, String subject, String body) {
//        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(sender);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }

    @Override
    public void sendEmailToAdmins( String subject, String body) {
        Set<Usuario> usuarios = usuarioRepository.findByRoles_Nombre("ROLE_ADMIN");
        message.setFrom(sender);
        message.setTo(usuarios.stream().map(Usuario::getEmail).toArray(String[]::new));
        message.setSubject(subject);
        message.setText(body);
//        mailSender.send(message);
    }

    @Override
    public void sendEmailToMultipleUsers(Set<Usuario> usuarios, String subject, String body){
        message.setFrom(sender);
        message.setTo(usuarios.stream().map(Usuario::getEmail).toArray(String[]::new));
        message.setSubject(subject);
        message.setText(body);
//        mailSender.send(message);
    }


}
