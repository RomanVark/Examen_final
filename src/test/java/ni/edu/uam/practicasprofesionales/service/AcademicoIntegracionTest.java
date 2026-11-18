package ni.edu.uam.practicasprofesionales.service;

import ni.edu.uam.practicasprofesionales.model.academico.*;
import ni.edu.uam.practicasprofesionales.persistence.JpaUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assumptions;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/** Pruebas reales de CRUD contra PostgreSQL; se omiten sin DB_PASSWORD. */
class AcademicoIntegracionTest {
    private final AcademicoService servicio = new AcademicoService();

    private void requireDb() {
        String password = System.getenv("DB_PASSWORD");
        Assumptions.assumeTrue(
            password != null && !password.isBlank(),
            "Configure DB_PASSWORD y la base PostgreSQL local.");
    }

    @org.junit.jupiter.api.AfterAll
    static void cerrarJpa() {
        JpaUtil.shutdown();
    }

    @Test
    void crudCarreraYEstudiante() {
        requireDb();
        String tag = UUID.randomUUID().toString().substring(0, 10).toUpperCase();

        Carrera carrera = new Carrera();
        carrera.setCodigo("T" + tag);
        carrera.setNombre("Carrera de prueba");
        carrera.setFacultad("Pruebas");
        carrera.setActiva(true);

        Carrera guardada = null;
        Estudiante estudiante = null;
        try {
            guardada = servicio.guardar(carrera);
            assertNotNull(guardada.getId());

            guardada.setNombre("Carrera editada");
            guardada = servicio.guardar(guardada);
            Long carreraId = guardada.getId();
            assertTrue(servicio.carreras().stream().anyMatch(
                actual -> actual.getId().equals(carreraId)
                    && actual.getNombre().equals("Carrera editada")));

            Estudiante nuevo = new Estudiante();
            nuevo.setCarnet("T-" + tag);
            nuevo.setNombres("Persona");
            nuevo.setApellidos("Prueba");
            nuevo.setCorreo("t" + tag.toLowerCase() + "@demo.example");
            nuevo.setCarrera(guardada);
            nuevo.setActivo(true);

            estudiante = servicio.guardar(nuevo);
            assertNotNull(estudiante.getId());
            Long estudianteId = estudiante.getId();
            assertTrue(servicio.buscarEstudiantes("Carrera editada").stream()
                .anyMatch(actual -> actual.getId().equals(estudianteId)));
        } finally {
            if (estudiante != null) servicio.eliminarEstudiante(estudiante.getId());
            if (guardada != null) servicio.eliminarCarrera(guardada.getId());
        }
    }

    @Test
    void crudTutorYCorreoUnico() {
        requireDb();
        String tag = UUID.randomUUID().toString().substring(0, 10).toLowerCase();

        TutorAcademico tutor = new TutorAcademico();
        tutor.setNombres("Tutor");
        tutor.setApellidos("Prueba");
        tutor.setCorreo("tutor-" + tag + "@demo.example");
        tutor.setEspecialidad("Ingeniería");
        tutor.setActivo(true);

        TutorAcademico guardado = null;
        try {
            guardado = servicio.guardar(tutor);
            assertNotNull(guardado.getId());

            guardado.setEspecialidad("Gestión");
            guardado = servicio.guardar(guardado);
            assertTrue(servicio.buscarTutores("tutor-" + tag).stream()
                .anyMatch(actual -> actual.getEspecialidad().equals("Gestión")));

            TutorAcademico duplicado = new TutorAcademico();
            duplicado.setNombres("Otro");
            duplicado.setApellidos("Tutor");
            duplicado.setCorreo(tutor.getCorreo());
            duplicado.setEspecialidad("Pruebas");
            duplicado.setActivo(true);

            assertThrows(IllegalArgumentException.class,
                () -> servicio.guardar(duplicado));
        } finally {
            if (guardado != null) servicio.eliminarTutor(guardado.getId());
        }
    }
}
