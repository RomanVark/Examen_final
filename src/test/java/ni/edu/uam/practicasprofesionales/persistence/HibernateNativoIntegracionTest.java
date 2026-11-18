package ni.edu.uam.practicasprofesionales.persistence;

import ni.edu.uam.practicasprofesionales.model.academico.Carrera;
import ni.edu.uam.practicasprofesionales.model.academico.Estudiante;
import ni.edu.uam.practicasprofesionales.model.academico.TutorAcademico;
import ni.edu.uam.practicasprofesionales.repository.AcademicoDao;
import ni.edu.uam.practicasprofesionales.repository.GenericDao;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/** Integración real: requiere DB_PASSWORD y SQL de las 12 tablas y carrera_tutor. */
class HibernateNativoIntegracionTest {
    private static void requierePostgres() {
        Assumptions.assumeTrue(
            System.getenv("DB_PASSWORD") != null && !System.getenv("DB_PASSWORD").isBlank(),
            "Configure PostgreSQL y ejecute sql/04_carrera_tutores.sql.");
    }

    @AfterAll static void cerrar() {
        HibernateUtil.cerrar();
    }

    private static Carrera crearCarrera(String clave) {
        Carrera carrera = new Carrera();
        carrera.setCodigo("HB" + clave);
        carrera.setNombre("Carrera laboratorio");
        carrera.setFacultad("Facultad de pruebas");
        carrera.setActiva(true);
        return carrera;
    }

    @Test void crudHqlYRelacionMuchosAMuchos() {
        requierePostgres();
        String clave = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        GenericDao<Carrera> carreras = new GenericDao<>(Carrera.class);
        GenericDao<TutorAcademico> tutores = new GenericDao<>(TutorAcademico.class);
        Carrera carrera = null;
        TutorAcademico tutor = null;
        try {
            carrera = carreras.save(crearCarrera(clave));
            assertNotNull(carrera.getId());
            carrera.setNombre("Carrera actualizada");
            carrera = carreras.save(carrera);
            assertEquals("Carrera actualizada", carreras.findById(carrera.getId()).getNombre());

            tutor = new TutorAcademico();
            tutor.setNombres("Tutor");
            tutor.setApellidos("Laboratorio");
            tutor.setCorreo("hib-" + clave.toLowerCase() + "@example.com");
            tutor.setEspecialidad("Pruebas");
            tutor.setActivo(true);
            tutor = tutores.save(tutor);

            try (Session session = HibernateUtil.openSession()) {
                Transaction tx = session.beginTransaction();
                try {
                    Carrera actual = session.find(Carrera.class, carrera.getId());
                    TutorAcademico asignado = session.find(TutorAcademico.class, tutor.getId());
                    actual.agregarTutor(asignado);
                    tx.commit();
                } catch (RuntimeException ex) {
                    if (tx.isActive()) tx.rollback();
                    throw ex;
                }
            }

            try (Session session = HibernateUtil.openSession()) {
                Carrera actual = session.createQuery(
                    "select c from Carrera c left join fetch c.tutores where c.id = :id", Carrera.class)
                    .setParameter("id", carrera.getId()).getSingleResult();
                assertEquals(1, actual.getTutores().size());
            }

            assertFalse(new AcademicoDao().estudiantesPorCarrera().isEmpty());

            // Una fábrica cerrada y reconstruida debe encontrar los mismos datos.
            Long id = carrera.getId();
            HibernateUtil.cerrar();
            assertEquals("Carrera actualizada", carreras.findById(id).getNombre());
        } finally {
            if (carrera != null && tutor != null) {
                try (Session session = HibernateUtil.openSession()) {
                    Transaction tx = session.beginTransaction();
                    try {
                        Carrera actual = session.find(Carrera.class, carrera.getId());
                        TutorAcademico asignado = session.find(TutorAcademico.class, tutor.getId());
                        if (actual != null && asignado != null) actual.quitarTutor(asignado);
                        tx.commit();
                    } catch (RuntimeException ex) {
                        if (tx.isActive()) tx.rollback();
                        throw ex;
                    }
                }
            }
            if (tutor != null) tutores.delete(tutor.getId());
            if (carrera != null) carreras.delete(carrera.getId());
        }
    }

    @Test void rollbackEvitaPersistirCambiosFallidos() {
        requierePostgres();
        String clave = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        try (Session session = HibernateUtil.openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                session.persist(crearCarrera(clave));
                session.flush();
                throw new IllegalStateException("Fallo simulado antes del commit");
            } catch (IllegalStateException esperado) {
                if (tx.isActive()) tx.rollback();
            }
        }
        try (Session session = HibernateUtil.openSession()) {
            Long total = session.createQuery(
                "select count(c) from Carrera c where c.codigo = :codigo", Long.class)
                .setParameter("codigo", "HB" + clave).getSingleResult();
            assertEquals(0L, total);
        }
    }
}
