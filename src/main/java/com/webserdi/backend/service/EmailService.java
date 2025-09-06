package com.webserdi.backend.service;

import com.webserdi.backend.entity.Usuario;

import java.util.Set;

public interface EmailService {
    void sendHtmlEmail(String to, String subject, String HtmlBody);


}
