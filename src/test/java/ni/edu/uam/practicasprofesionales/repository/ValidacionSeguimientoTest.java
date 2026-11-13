package ni.edu.uam.practicasprofesionales.repository;

import ni.edu.uam.practicasprofesionales.model.asignaciones.Practica;
import ni.edu.uam.practicasprofesionales.model.seguimiento.Bitacora;
import ni.edu.uam.practicasprofesionales.model.seguimiento.Documento;
import ni.edu.uam.practicasprofesionales.model.seguimiento.Evaluacion;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class ValidacionSeguimientoTest {
    private Practica practica() { return new Practica(); }

    @Test void aceptaBitacoraConHorasDentroDelRango() {
        Bitacora b = new Bitacora();
        b.setPractica(practica());
        b.setFecha(LocalDate.of(2026, 11, 1));
        b.setActividad("Documentación de requerimientos");
        b.setHoras(8);
        b.setValidada(Boolean.FALSE);
        assertDoesNotThrow(() -> ValidacionSeguimiento.validar(b));
    }

    @Test void rechazaBitacoraConCeroHoras() {
        Bitacora b = new Bitacora();
        b.setPractica(practica());
        b.setFecha(LocalDate.of(2026, 11, 1));
        b.setActividad("Actividad de prueba");
        b.setHoras(0);
        b.setValidada(Boolean.FALSE);
        assertThrows(IllegalArgumentException.class, () -> ValidacionSeguimiento.validar(b));
    }

    @Test void rechazaBitacoraConMasDe24Horas() {
        Bitacora b = new Bitacora();
        b.setPractica(practica());
        b.setFecha(LocalDate.of(2026, 11, 1));
        b.setActividad("Actividad de prueba");
        b.setHoras(25);
        b.setValidada(Boolean.FALSE);
        assertThrows(IllegalArgumentException.class, () -> ValidacionSeguimiento.validar(b));
    }

    @Test void aceptaCalificacionEntreCeroYCien() {
        Evaluacion e = new Evaluacion();
        e.setPractica(practica());
        e.setFecha(LocalDate.of(2026, 11, 2));
        e.setEvaluador("Tutor de prueba");
        e.setCalificacion(new BigDecimal("95.50"));
        assertDoesNotThrow(() -> ValidacionSeguimiento.validar(e));
    }

    @Test void rechazaCalificacionMayorQueCien() {
        Evaluacion e = new Evaluacion();
        e.setPractica(practica());
        e.setFecha(LocalDate.of(2026, 11, 2));
        e.setEvaluador("Tutor de prueba");
        e.setCalificacion(new BigDecimal("105"));
        assertThrows(IllegalArgumentException.class, () -> ValidacionSeguimiento.validar(e));
    }

    @Test void rechazaDocumentoSinRuta() {
        Documento d = new Documento();
        d.setPractica(practica());
        d.setTipo("Informe final");
        d.setNombreArchivo("informe.pdf");
        d.setFechaEntrega(LocalDate.of(2026, 11, 3));
        d.setVerificado(Boolean.FALSE);
        assertThrows(IllegalArgumentException.class, () -> ValidacionSeguimiento.validar(d));
    }
}
