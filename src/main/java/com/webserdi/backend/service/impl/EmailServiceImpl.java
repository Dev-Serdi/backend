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
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import java.util.Set;

@EnableAsync
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

    @Async
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

}
