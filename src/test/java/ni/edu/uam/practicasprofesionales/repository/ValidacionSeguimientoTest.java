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
    private Practica practicaRegistrada() {
        Practica practica = new Practica();
        practica.setId(1L);
        return practica;
    }

    private Bitacora bitacoraValida(int horas) {
        Bitacora b = new Bitacora();
        b.setPractica(practicaRegistrada());
        b.setFecha(LocalDate.of(2026, 11, 1));
        b.setActividad("Documentación de requerimientos");
        b.setHoras(horas);
        b.setValidada(Boolean.FALSE);
        return b;
    }

    private Evaluacion evaluacionValida(String nota) {
        Evaluacion e = new Evaluacion();
        e.setPractica(practicaRegistrada());
        e.setFecha(LocalDate.of(2026, 11, 2));
        e.setEvaluador("Tutor de prueba");
        e.setCalificacion(new BigDecimal(nota));
        return e;
    }

    @Test void aceptaBitacoraConUnaHora() {
        assertDoesNotThrow(() -> ValidacionSeguimiento.validar(bitacoraValida(1)));
    }

    @Test void aceptaBitacoraConVeinticuatroHoras() {
        assertDoesNotThrow(() -> ValidacionSeguimiento.validar(bitacoraValida(24)));
    }

    @Test void rechazaBitacoraConCeroHoras() {
        assertThrows(IllegalArgumentException.class,
            () -> ValidacionSeguimiento.validar(bitacoraValida(0)));
    }

    @Test void rechazaBitacoraConMasDe24Horas() {
        assertThrows(IllegalArgumentException.class,
            () -> ValidacionSeguimiento.validar(bitacoraValida(25)));
    }

    @Test void rechazaBitacoraSinPracticaRegistrada() {
        Bitacora b = bitacoraValida(8);
        b.setPractica(new Practica());
        assertThrows(IllegalArgumentException.class, () -> ValidacionSeguimiento.validar(b));
    }

    @Test void aceptaCalificacionCeroYCien() {
        assertDoesNotThrow(() -> ValidacionSeguimiento.validar(evaluacionValida("0")));
        assertDoesNotThrow(() -> ValidacionSeguimiento.validar(evaluacionValida("100")));
    }

    @Test void rechazaCalificacionMenorQueCero() {
        assertThrows(IllegalArgumentException.class,
            () -> ValidacionSeguimiento.validar(evaluacionValida("-0.01")));
    }

    @Test void rechazaCalificacionMayorQueCien() {
        assertThrows(IllegalArgumentException.class,
            () -> ValidacionSeguimiento.validar(evaluacionValida("100.01")));
    }

    @Test void rechazaEvaluacionSinPracticaRegistrada() {
        Evaluacion e = evaluacionValida("95.50");
        e.setPractica(new Practica());
        assertThrows(IllegalArgumentException.class, () -> ValidacionSeguimiento.validar(e));
    }

    @Test void rechazaDocumentoSinRuta() {
        Documento d = new Documento();
        d.setPractica(practicaRegistrada());
        d.setTipo("Informe final");
        d.setNombreArchivo("informe.pdf");
        d.setFechaEntrega(LocalDate.of(2026, 11, 3));
        d.setVerificado(Boolean.FALSE);
        assertThrows(IllegalArgumentException.class, () -> ValidacionSeguimiento.validar(d));
    }

    @Test void rechazaDocumentoSinPracticaRegistrada() {
        Documento d = new Documento();
        d.setPractica(new Practica());
        d.setTipo("Informe final");
        d.setNombreArchivo("informe.pdf");
        d.setRutaArchivo("documentos/informe.pdf");
        d.setFechaEntrega(LocalDate.of(2026, 11, 3));
        d.setVerificado(Boolean.FALSE);
        assertThrows(IllegalArgumentException.class, () -> ValidacionSeguimiento.validar(d));
    }
}
