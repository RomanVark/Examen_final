package ni.edu.uam.practicasprofesionales.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import ni.edu.uam.practicasprofesionales.model.academico.TutorAcademico;
import ni.edu.uam.practicasprofesionales.model.alianzas.SupervisorEmpresa;
import ni.edu.uam.practicasprofesionales.model.asignaciones.OfertaPractica;
import ni.edu.uam.practicasprofesionales.model.asignaciones.Postulacion;
import ni.edu.uam.practicasprofesionales.model.asignaciones.Practica;
import ni.edu.uam.practicasprofesionales.model.enums.EstadoOferta;
import ni.edu.uam.practicasprofesionales.model.enums.EstadoPostulacion;
import ni.edu.uam.practicasprofesionales.model.enums.EstadoPractica;
import ni.edu.uam.practicasprofesionales.persistence.JpaUtil;
import ni.edu.uam.practicasprofesionales.repository.ValidacionDeNegocio;

/**
 * Caso de uso de asignaciones. La aceptación de una postulación y la creación
 * de la práctica se confirman en una sola transacción.
 */
public class AsignacionesService {

    public Practica aceptarYAsignar(Long postulacionId, Long tutorId,
                                    Long supervisorId, Integer horasRequeridas) {
        if (postulacionId == null || tutorId == null || supervisorId == null)
            throw new IllegalArgumentException("Debe indicar postulación, tutor y supervisor.");

        try (EntityManager em = JpaUtil.open()) {
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();

                Postulacion postulacion = em.find(Postulacion.class, postulacionId);
                if (postulacion == null) throw new IllegalArgumentException("La postulación no existe.");
                if (postulacion.getEstado() != EstadoPostulacion.PENDIENTE)
                    throw new IllegalArgumentException("Solo se pueden procesar postulaciones PENDIENTES.");

                OfertaPractica oferta = postulacion.getOferta();
                if (oferta.getEstado() != EstadoOferta.ABIERTA)
                    throw new IllegalArgumentException("La oferta no está ABIERTA.");

                Long practicasAsignadas = em.createQuery(
                        "select count(p) from Practica p where p.oferta.id = :oferta", Long.class)
                        .setParameter("oferta", oferta.getId())
                        .getSingleResult();
                if (practicasAsignadas >= oferta.getCupos())
                    throw new IllegalArgumentException("La oferta ya alcanzó el límite de cupos.");

                TutorAcademico tutor = em.find(TutorAcademico.class, tutorId);
                SupervisorEmpresa supervisor = em.find(SupervisorEmpresa.class, supervisorId);
                if (tutor == null) throw new IllegalArgumentException("El tutor académico no existe.");
                if (supervisor == null) throw new IllegalArgumentException("El supervisor de empresa no existe.");

                Practica practica = new Practica();
                practica.setEstudiante(postulacion.getEstudiante());
                practica.setOferta(oferta);
                practica.setTutorAcademico(tutor);
                practica.setSupervisorEmpresa(supervisor);
                practica.setFechaInicio(oferta.getFechaInicio());
                practica.setFechaFin(oferta.getFechaFin());
                practica.setHorasRequeridas(horasRequeridas);
                practica.setEstado(EstadoPractica.PLANIFICADA);

                ValidacionDeNegocio.validar(practica);
                ValidacionDeNegocio.validarUnicidad(em, practica);

                postulacion.setEstado(EstadoPostulacion.ACEPTADA);
                em.persist(practica);
                tx.commit();
                return practica;
            } catch (RuntimeException ex) {
                if (tx.isActive()) tx.rollback();
                throw ex;
            }
        }
    }
}
