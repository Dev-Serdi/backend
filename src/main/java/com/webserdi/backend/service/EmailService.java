package com.webserdi.backend.service;

import com.webserdi.backend.entity.Usuario;

import java.util.Set;

public interface EmailService {
    void sendHtmlEmail(String to, String subject, String HtmlBody);
    void sendEmailToUser(String to, String subject, String body);
    void sendEmailToAdmins( String subject, String body);
    void sendEmailToMultipleUsers(Set<Usuario> usuarios, String subject, String body);


}
