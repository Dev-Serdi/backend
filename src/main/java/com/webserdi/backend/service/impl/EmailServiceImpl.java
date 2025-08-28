package com.webserdi.backend.service.impl;
import com.webserdi.backend.entity.Usuario;
import com.webserdi.backend.repository.UsuarioRepository;
import com.webserdi.backend.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
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
    public void sendEmailToUser(String to, String subject, String body) {
//        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(sender);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
//        mailSender.send(message);
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
