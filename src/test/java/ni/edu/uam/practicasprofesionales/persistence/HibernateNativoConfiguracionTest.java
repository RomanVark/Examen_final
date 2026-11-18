package ni.edu.uam.practicasprofesionales.persistence;

import ni.edu.uam.practicasprofesionales.model.academico.Carrera;
import ni.edu.uam.practicasprofesionales.model.academico.Estudiante;
import ni.edu.uam.practicasprofesionales.model.academico.TutorAcademico;
import ni.edu.uam.practicasprofesionales.repository.GenericDao;
import ni.edu.uam.practicasprofesionales.repository.JpaRepository;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

/** Verificaciones estructurales de Hibernate nativo; no requieren PostgreSQL. */
class HibernateNativoConfiguracionTest {
    @Test void fabricaNativaYAdaptadorCompartenContrato() throws Exception {
        assertEquals(SessionFactory.class,
            HibernateUtil.class.getMethod("getSessionFactory").getReturnType());
        assertEquals(GenericDao.class, JpaRepository.class.getSuperclass());
        assertTrue(SessionFactory.class.isAssignableFrom(
            JpaUtil.class.getMethod("factory").getReturnType()));
    }

    @Test void registraLasDoceEntidadesDelDominio() throws Exception {
        try (InputStream in = getClass().getResourceAsStream("/hibernate.cfg.xml")) {
            assertNotNull(in, "Falta hibernate.cfg.xml");
            String xml = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            assertEquals(12, Pattern.compile("<mapping class=").matcher(xml).results().count());
            assertTrue(xml.contains("model.academico.Carrera"));
            assertTrue(xml.contains("model.alianzas.Empresa"));
            assertTrue(xml.contains("model.seguimiento.Documento"));
            assertTrue(xml.contains("name=\"hibernate.hbm2ddl.auto\">validate"));
        }
    }

    @Test void relacionesBidireccionalesSeSincronizan() throws Exception {
        Carrera carrera = new Carrera();
        TutorAcademico tutor = new TutorAcademico();
        carrera.agregarTutor(tutor);
        assertTrue(carrera.getTutores().contains(tutor));
        assertTrue(tutor.getCarreras().contains(carrera));
        carrera.quitarTutor(tutor);
        assertFalse(carrera.getTutores().contains(tutor));
        assertFalse(tutor.getCarreras().contains(carrera));

        Estudiante estudiante = new Estudiante();
        carrera.agregarEstudiante(estudiante);
        assertSame(carrera, estudiante.getCarrera());
        assertTrue(carrera.getEstudiantes().contains(estudiante));
        carrera.quitarEstudiante(estudiante);
        assertNull(estudiante.getCarrera());
        assertFalse(carrera.getEstudiantes().contains(estudiante));

        OneToMany unoMuchos = Carrera.class.getDeclaredField("estudiantes")
            .getAnnotation(OneToMany.class);
        assertEquals("carrera", unoMuchos.mappedBy());
        JoinTable tabla = Carrera.class.getDeclaredField("tutores")
            .getAnnotation(JoinTable.class);
        assertEquals("carrera_tutor", tabla.name());
        ManyToMany inversa = TutorAcademico.class.getDeclaredField("carreras")
            .getAnnotation(ManyToMany.class);
        assertEquals("tutores", inversa.mappedBy());
    }
}
