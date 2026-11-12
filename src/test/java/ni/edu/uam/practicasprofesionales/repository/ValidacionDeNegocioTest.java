package ni.edu.uam.practicasprofesionales.repository;

import ni.edu.uam.practicasprofesionales.model.academico.*;
import ni.edu.uam.practicasprofesionales.model.alianzas.*;
import ni.edu.uam.practicasprofesionales.model.asignaciones.*;
import ni.edu.uam.practicasprofesionales.model.seguimiento.*;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class ValidacionDeNegocioTest {
    @Test void evaluacionInvalidaSeRechaza() {
        Evaluacion e = new Evaluacion();
        e.setEvaluador("Docente de prueba");
        e.setCalificacion(new BigDecimal("110"));
        e.setFecha(LocalDate.of(2026, 10, 1));
        e.setPractica(new Practica());
        assertThrows(IllegalArgumentException.class, () -> ValidacionDeNegocio.validar(e));
    }
    @Test void fechasEnOrdenIncorrectoSeRechazan() {
        Convenio c = new Convenio();
        c.setCodigo("C-TEST");
        c.setEmpresa(new Empresa());
        c.setFechaInicio(LocalDate.of(2026, 12, 1));
        c.setFechaFin(LocalDate.of(2026, 1, 1));
        c.setEstado(ni.edu.uam.practicasprofesionales.model.enums.EstadoConvenio.VIGENTE);
        assertThrows(IllegalArgumentException.class, () -> ValidacionDeNegocio.validar(c));
    }
    @Test void bitacoraConHorasValidasSeAcepta() {
        Bitacora b = new Bitacora();
        b.setPractica(new Practica());
        b.setFecha(LocalDate.of(2026, 9, 1));
        b.setActividad("Análisis de requisitos");
        b.setHoras(6);
        b.setValidada(true);
        assertDoesNotThrow(() -> ValidacionDeNegocio.validar(b));
    }
    @Test void correoInvalidoSeRechaza() {
        TutorAcademico tutor = new TutorAcademico();
        tutor.setNombres("Ana"); tutor.setApellidos("Pérez");
        tutor.setCorreo("correo-sin-arroba"); tutor.setEspecialidad("Sistemas"); tutor.setActivo(true);
        assertThrows(IllegalArgumentException.class, () -> ValidacionDeNegocio.validar(tutor));
    }
}
