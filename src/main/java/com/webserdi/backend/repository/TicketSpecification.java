package com.webserdi.backend.repository;

import com.webserdi.backend.entity.Ticket;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TicketSpecification {

    public Specification<Ticket> getTickets(
            Long departamentoId, Long creadorId, Long asignadoId,
            Long estadoId, Long prioridadId, Boolean isAuthorized,
            Boolean hasResponse, String ubicacion) {

        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (departamentoId != null) {
                predicates.add(criteriaBuilder.equal(root.get("departamento").get("id"), departamentoId));
            }
            if (creadorId != null) {
                predicates.add(criteriaBuilder.equal(root.get("usuarioCreador").get("id"), creadorId));
            }
            if (asignadoId != null) {
                predicates.add(criteriaBuilder.equal(root.get("usuarioAsignado").get("id"), asignadoId));
            }
            if (estadoId != null) {
                predicates.add(criteriaBuilder.equal(root.get("estado").get("id"), estadoId));
            }
            if (prioridadId != null) {
                predicates.add(criteriaBuilder.equal(root.get("prioridad").get("id"), prioridadId));
            }
            if (isAuthorized != null) {
                predicates.add(criteriaBuilder.equal(root.get("isAuthorized"), isAuthorized));
            }
            if (hasResponse != null) {
                if (hasResponse) {
                    predicates.add(criteriaBuilder.isNotNull(root.get("fechaRespuesta")));
                } else {
                    predicates.add(criteriaBuilder.isNull(root.get("fechaRespuesta")));
                }
            }
            if (ubicacion != null) {
                predicates.add(criteriaBuilder.equal(root.get("ubicacion"), ubicacion));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}