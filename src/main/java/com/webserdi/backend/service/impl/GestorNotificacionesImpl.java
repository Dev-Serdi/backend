package com.webserdi.backend.service.impl;

import com.webserdi.backend.entity.Ticket;
import com.webserdi.backend.entity.TipoNotificacion;
import com.webserdi.backend.entity.Usuario;
import com.webserdi.backend.service.EmailService;
import com.webserdi.backend.service.GestorNotificacionesService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class GestorNotificacionesImpl implements GestorNotificacionesService {

    private final EmailService emailService;
    private final ResolvedorDestinatariosServiceImpl resolvedorDestinatarios; // Inyectar el resolvedor
    private final NotificationServiceImpl notificationService;
    private final SpringTemplateEngine templateEngine;



    @Override
    public void dispatch(EventoNotificacionServiceImpl evento, Usuario usuarioNuevoAsignado) {
        Set<Usuario> destinatarios = resolvedorDestinatarios.resolver(evento);
        // 1. Obtener la lista de destinatarios según las reglas de negocio
        Ticket ticket = (evento.getData() instanceof Ticket) ? (Ticket) evento.getData() : null;
        String ruta = (ticket != null) ? "/helpdesk/task/" + ticket.getId() : (usuarioNuevoAsignado!= null)? "/perfil/"+ usuarioNuevoAsignado.getId(): "/perfil";

        // 2. Construir el mensaje (esto podría moverse a otra clase si se vuelve complejo)
        String titulo = construirTitulo(evento);
        String cuerpo = construirCuerpo(evento);
        if(Optional.ofNullable(usuarioNuevoAsignado).isPresent() && (evento.getTipo() == TipoNotificacion.REASIGNACION_DEPARTAMENTO_TICKET || evento.getTipo() == TipoNotificacion.REASIGNACION_USUARIO_TICKET)) {
            notificationService.sendNotification(usuarioNuevoAsignado, ticket, titulo, cuerpo, ruta);
        }
        if (destinatarios.isEmpty()) {
            return;
        }
        String asunto = construirAsunto(evento);
        // 3. Enviar correo, respetando si es configurable o no
        for (Usuario destinatario : destinatarios) {
            boolean debeEnviar = false;
            if (evento.getTipo().isConfigurable()) {
                // Si es configurable, revisar las preferencias personales del usuario
                if (destinatario.getNotificacionesActivas().contains(evento.getTipo())) {
                    debeEnviar = true;
                }
            } else {
                // Si NO es configurable, es una notificación de sistema y se debe enviar siempre.
                debeEnviar = true;
            }

            if (debeEnviar) {
                String htmlBody = construirCuerpoHtml(evento, destinatario, usuarioNuevoAsignado);
                notificationService.sendNotification(destinatario, ticket, titulo, cuerpo, ruta);
                emailService.sendHtmlEmail(destinatario.getEmail(), asunto, htmlBody);
            }
        }
    }

    // Métodos helper para construir los mensajes de correo
    private String construirTitulo(EventoNotificacionServiceImpl evento) {
        Ticket ticket = (evento.getData() instanceof Ticket) ? (Ticket) evento.getData() : null;
        String codigo = (ticket != null) ? ticket.getCodigo() + ": " : "";
        return codigo + evento.getTipo().getTitulo();
    }

    private String construirCuerpo(EventoNotificacionServiceImpl evento) {
        // Aquí puedes desarrollar una lógica más elaborada para generar el cuerpo del correo
        // basándote en el tipo de evento y los datos.
        // Por ahora, un mensaje genérico:
        Ticket ticket = (evento.getData() instanceof Ticket) ? (Ticket) evento.getData() : null;
        if (ticket != null) {
            return ticket.getCodigo() + ": " + evento.getTipo().getTitulo() + ". Por favor, revisa la plataforma para más detalles";
        }
        Usuario usuario = (evento.getData() instanceof Usuario) ? (Usuario) evento.getData() : null;

        switch (evento.getTipo()){
            case PERFIL_MODIFICADO :
                return "Hola "+ usuario.getNombre()+" "+usuario.getApellido()+", tu perfil ha sido actualizado. Por favor, recarga la página.";
            case USUARIO_MODIFICADO:
                return "El perfil de: "+ usuario.getNombre()+" "+usuario.getApellido()+", ha sido actualizado por un administrador.";
            case NUEVO_USUARIO_REGISTRADO:
                return "Se ha registrado "+usuario.getNombre()+" "+usuario.getApellido()+", favor de revisar su informacion.";
        }

        return null;
    }
    private String construirAsunto(EventoNotificacionServiceImpl evento) {
        Ticket ticket = (evento.getData() instanceof Ticket) ? (Ticket) evento.getData() : null;
        Usuario usuario = (evento.getData() instanceof Usuario) ? (Usuario) evento.getData() : null;

        switch (evento.getTipo()) {
            case NUEVO_TICKET_ASIGNADO:
            case CAMBIO_ESTADO_TICKET:
            case NUEVO_MENSAJE_EN_TICKET:
            case TICKET_NO_AUTORIZADO:
                return String.format("%s - Ticket #%s", evento.getTipo().getTitulo(), ticket.getCodigo());
            case NUEVO_USUARIO_REGISTRADO:
                return String.format("Nuevo Usuario: %s %s", usuario.getNombre(), usuario.getApellido());
            case PERFIL_MODIFICADO:
                return "Hola "+ usuario.getNombre()+" "+usuario.getApellido()+". Tu información de perfil ha sido actualizada, favor de revisar la plataforma.";
            case USUARIO_MODIFICADO:
                return "El usuario "+ usuario.getNombre()+" "+usuario.getApellido()+"\n ha sido modificado";
            default:
                return evento.getTipo().getTitulo();
        }
    }

    private String construirCuerpoHtml(EventoNotificacionServiceImpl evento, Usuario destinatario, Usuario usuarioNuevoAsignado) {
        final Context context = new Context();
        Ticket ticket = (evento.getData() instanceof Ticket) ? (Ticket) evento.getData() : null;
        Usuario usuarioData = (evento.getData() instanceof Usuario) ? (Usuario) evento.getData() : null;

        String mensaje = "";
        // <-- IMPORTANTE: Cambia esta URL
        String ctaUrl = "https://mds.serdi.com.mx";
        String ctaTexto = "Ir a la Plataforma";

        switch (evento.getTipo()) {
            case NUEVO_TICKET_ASIGNADO:
                mensaje = String.format(
                        "<p>Hola <strong>%s</strong>,</p>" +
                                "<p>Se te ha asignado un nuevo ticket de soporte:</p>" +
                                "<ul>" +
                                "<li><b>Ticket:</b> #%s</li>" +
                                "<li><b>Asunto:</b> %s</li>" +
                                "<li><b>Creado por:</b> %s %s</li>" +
                                "</ul>",
                        destinatario.getNombre(), ticket.getCodigo(), ticket.getTema(),
                        ticket.getUsuarioCreador().getNombre(), ticket.getUsuarioCreador().getApellido());
                ctaTexto = "Ver Ticket";
                ctaUrl += "/helpdesk/task/" + ticket.getId();
                break;
            case CAMBIO_ESTADO_TICKET:
                mensaje = String.format(
                        "<p>Hola <strong>%s</strong>,</p>" +
                                "<p>El estado del ticket <b>#%s</b> ha cambiado a:</p>" +
                                "<h2 style='color: #0056b3;'>%s</h2>",
                        destinatario.getNombre(), ticket.getCodigo(), ticket.getEstado().getNombre());
                ctaTexto = "Revisar Ticket";
                ctaUrl += "/helpdesk/task/" + ticket.getId();
                break;
            case FECHA_COMPROMISO_ASIGNADA:
                mensaje = String.format(
                        "<p>Hola <strong>%s</strong>,</p>" +
                                "<p>Se ha asignado fecha de compromiso al ticket <b>#%s</b></p>" +
                                "<h2 style='color: #0056b3;'>%s</h2>",
                        destinatario.getNombre(), ticket.getCodigo(), ticket.getFechaCompromiso());
                ctaTexto = "Revisar Ticket";
                ctaUrl += "/helpdesk/task/" + ticket.getId();
                break;
            case NUEVO_MENSAJE_EN_TICKET:
                mensaje = String.format(
                        "<p>Hola <strong>%s</strong>,</p>" +
                                "<p>Se ha añadido un nuevo comentario en el ticket <b>#%s</b>.</p>"+
                                "<p>Asunto: %s</p>",
                        destinatario.getNombre(), ticket.getCodigo(), ticket.getTema());
                ctaTexto = "Leer Comentario";
                ctaUrl += "/helpdesk/task/" + ticket.getId();
                break;
            case NUEVO_USUARIO_REGISTRADO:
                mensaje = String.format(
                        "<p>Hola Administrador,</p>" +
                                "<p>Un nuevo usuario se ha registrado:</p>" +
                                "<ul>" +
                                "<li><b>Nombre:</b> %s %s</li>" +
                                "<li><b>Email:</b> %s</li>" +
                                "</ul>",
                        usuarioData.getNombre(), usuarioData.getApellido(), usuarioData.getEmail());
                ctaTexto = "Gestionar Usuario";
                ctaUrl += "/admin/edit-user/" + usuarioData.getId();
                break;
            case TICKET_NO_AUTORIZADO:
                mensaje = String.format(
                        "<p>Hola <strong>%s</strong>,</p>" +
                                "<p>El ticket: <b>#%s</b>.</p>" +
                                "<p>Ha sido marcado como <b>no autorizado</b>.</p>" +
                                "<p>Favor de revisar la plataforma para más información.</p>",
                        destinatario.getNombre(), ticket != null ? ticket.getCodigo() : "");
                ctaTexto = "Ir a la Plataforma";
                break;
            case PERFIL_MODIFICADO:
                mensaje = String.format(
                        "<p>Hola <strong>%s</strong>,</p>" +
                                "<p>Tu información de perfil ha sido actualizada. Si no reconoces esta actividad, contacta a un administrador.</p>",
                        destinatario.getNombre());
                ctaTexto = "Ver Mi Perfil";
                ctaUrl += "/perfil/" + destinatario.getId();
                break;
            case TICKET_MODIFICADO:
                mensaje = String.format(
                        "<p>Hola <strong>%s</strong>,</p>" +
                                "<p>El ticket en el cual estás asignado ha sido modificado. Verifica su información.</p>",
                        destinatario.getNombre());
                ctaTexto = "Revisar Ticket";
                ctaUrl += "/helpdesk/task/" + (ticket != null ? ticket.getId() : "");
                break;
            case REASIGNACION_USUARIO_TICKET:
                mensaje = String.format(
                        "<p>Hola.</p>" +
                                "<p>El ticket <b>%s</b>, cambió de <strong>%s</strong> a <strong>%s</strong>. Verifica su información.</p>",
                        ticket != null ? ticket.getCodigo() : "",
                        ticket != null ? ticket.getUsuarioAsignado(): "",
                        usuarioNuevoAsignado != null ? usuarioNuevoAsignado.getNombre() : destinatario.getNombre());
                ctaTexto = "Revisar Ticket";
                ctaUrl += "/helpdesk/task/" + (ticket != null ? ticket.getId() : "");
                break;
            case REASIGNACION_DEPARTAMENTO_TICKET:
                mensaje = String.format(
                        "<p>Hola.</p>" +
                                "<p>El ticket <b>%s</b> ha sido reasignado al departamento <b>%s</b>.</p>" +
                                "<p>Se asignó el usuario <b>%s</b>. Verifica su información.</p>",
                        "<p>Hola.</p>" +
                                "<p>El ticket <b>%s</b> ha sido reasignado al departamento <b>%s</b>.</p>" +
                                "<p>Se asignó el usuario <b>%s</b>. Verifica su información.</p>",
                        ticket != null ? ticket.getCodigo() : "",
                        ticket != null && ticket.getDepartamento() != null ? ticket.getDepartamento().getNombre() : "",
                        ticket != null && ticket.getUsuarioAsignado() != null ? ticket.getUsuarioAsignado().getNombre() : ""
                );
                        ticket != null && ticket.getDepartamento() != null ? ticket.getDepartamento().getNombre() : "",
                        ticket != null && ticket.getUsuarioAsignado() != null ? ticket.getUsuarioAsignado().getNombre() : ""
                );
                ctaTexto = "Revisar Ticket";
                ctaUrl += "/helpdesk/task/" + (ticket != null ? ticket.getId() : "");
                break;


            case NUEVO_TICKET_CREADO:
                mensaje = String.format(
                        "<p>Hola <strong>%s</strong>,</p>" +
                                "<p>Se ha creado un nuevo ticket con el código <b>%s</b>. Revisa los detalles en la plataforma.</p>",
                        destinatario.getNombre(), ticket != null ? ticket.getCodigo() : "");
                ctaTexto = "Ver Ticket";
                ctaUrl += "/helpdesk/task/" + (ticket != null ? ticket.getId() : "");
                break;
            case USUARIO_MODIFICADO:
                mensaje = String.format(
                        "<p>Hola <strong>%s</strong>,</p>" +
                                "<p>Tu usuario ha sido modificado por un administrador. Si no reconoces esta acción, contacta soporte.</p>",
                        destinatario.getNombre());
                ctaTexto = "Ver Mi Perfil";
                ctaUrl += "/perfil/" + destinatario.getId();
                break;
            default:
                mensaje = "<p>Hay una nueva notificación en la plataforma.</p>";
                break;
        }

        context.setVariable("tituloPrincipal", evento.getTipo().getTitulo());
        context.setVariable("mensajeCuerpo", mensaje);
        context.setVariable("ctaTexto", ctaTexto);
        context.setVariable("ctaUrl", ctaUrl);

        return templateEngine.process("email-template.html", context);
    }
}