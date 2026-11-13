package ni.edu.uam.practicasprofesionales.repository;

import ni.edu.uam.practicasprofesionales.model.academico.*;
import ni.edu.uam.practicasprofesionales.model.alianzas.*;
import ni.edu.uam.practicasprofesionales.model.asignaciones.*;
import ni.edu.uam.practicasprofesionales.model.seguimiento.*;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDate;
import ni.edu.uam.practicasprofesionales.model.enums.EstadoConvenio;
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
        Empresa empresa = new Empresa();
        empresa.setId(1L);
        c.setEmpresa(empresa);
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
    @Test void empresaConRucInvalidoSeRechaza() {
        Empresa empresa = empresaValida("RUC CON ESPACIOS");
        assertThrows(IllegalArgumentException.class, () -> ValidacionDeNegocio.validar(empresa));
    }

    @Test void rucSeNormalizaParaComparacion() {
        assertEquals("DEMO-001", ValidacionDeNegocio.normalizarRuc("  demo-001  "));
    }

    @Test void convenioVigenteFueraDeFechasSeRechaza() {
        Convenio convenio = convenioValido();
        convenio.setFechaInicio(LocalDate.now().minusYears(2));
        convenio.setFechaFin(LocalDate.now().minusDays(1));
        assertThrows(IllegalArgumentException.class, () -> ValidacionDeNegocio.validar(convenio));
    }

    @Test void convenioVigenteDentroDeFechasSeAcepta() {
        Convenio convenio = convenioValido();
        convenio.setFechaInicio(LocalDate.now().minusDays(1));
        convenio.setFechaFin(LocalDate.now().plusDays(30));
        assertDoesNotThrow(() -> ValidacionDeNegocio.validar(convenio));
    }

    @Test void supervisorSinEmpresaRegistradaSeRechaza() {
        SupervisorEmpresa supervisor = new SupervisorEmpresa();
        supervisor.setNombres("Ana");
        supervisor.setApellidos("Pérez");
        supervisor.setCorreo("ana@example.com");
        supervisor.setCargo("Coordinadora");
        supervisor.setActivo(true);
        supervisor.setEmpresa(new Empresa());
        assertThrows(IllegalArgumentException.class, () -> ValidacionDeNegocio.validar(supervisor));
    }


    @Test void bitacoraRechazaHorasMayoresA24() {
        Bitacora b = new Bitacora();
        Practica practica = new Practica();
        practica.setId(1L);
        b.setPractica(practica);
        b.setFecha(LocalDate.of(2026, 9, 1));
        b.setActividad("Actividad de prueba");
        b.setHoras(25);
        b.setValidada(false);
        assertThrows(IllegalArgumentException.class, () -> ValidacionDeNegocio.validar(b));
    }

    @Test void bitacoraRequiereUnaPracticaRegistrada() {
        Bitacora b = new Bitacora();
        b.setPractica(new Practica());
        b.setFecha(LocalDate.of(2026, 9, 1));
        b.setActividad("Actividad de prueba");
        b.setHoras(4);
        b.setValidada(false);
        assertThrows(IllegalArgumentException.class, () -> ValidacionDeNegocio.validar(b));
    }

    @Test void evaluacionAceptaLimiteSuperiorDeCien() {
        Evaluacion e = new Evaluacion();
        Practica practica = new Practica();
        practica.setId(1L);
        e.setPractica(practica);
        e.setFecha(LocalDate.of(2026, 9, 1));
        e.setEvaluador("Docente de prueba");
        e.setCalificacion(new BigDecimal("100"));
        assertDoesNotThrow(() -> ValidacionDeNegocio.validar(e));
    }

    @Test void evaluacionRequiereUnaPracticaRegistrada() {
        Evaluacion e = new Evaluacion();
        e.setPractica(new Practica());
        e.setFecha(LocalDate.of(2026, 9, 1));
        e.setEvaluador("Docente de prueba");
        e.setCalificacion(new BigDecimal("85"));
        assertThrows(IllegalArgumentException.class, () -> ValidacionDeNegocio.validar(e));
    }

    @Test void documentoRequierePracticaRegistrada() {
        Documento d = new Documento();
        d.setPractica(new Practica());
        d.setTipo("Informe final");
        d.setNombreArchivo("informe.pdf");
        d.setRutaArchivo("documentos/informe.pdf");
        d.setFechaEntrega(LocalDate.of(2026, 9, 1));
        d.setVerificado(false);
        assertThrows(IllegalArgumentException.class, () -> ValidacionDeNegocio.validar(d));
    }

    @Test void documentoRequiereDatosObligatorios() {
        Documento d = new Documento();
        Practica practica = new Practica();
        practica.setId(1L);
        d.setPractica(practica);
        d.setNombreArchivo("informe.pdf");
        d.setRutaArchivo("documentos/informe.pdf");
        d.setFechaEntrega(LocalDate.of(2026, 9, 1));
        d.setVerificado(false);
        assertThrows(IllegalArgumentException.class, () -> ValidacionDeNegocio.validar(d));
    }

    private static Empresa empresaValida(String ruc) {
        Empresa empresa = new Empresa();
        empresa.setRuc(ruc);
        empresa.setRazonSocial("Empresa de prueba");
        empresa.setSector("Tecnología");
        empresa.setCorreo("contacto@example.com");
        empresa.setActiva(true);
        return empresa;
    }

    private static Convenio convenioValido() {
        Convenio convenio = new Convenio();
        convenio.setCodigo("CONV-TEST");
        Empresa empresa = empresaValida("DEMO-TEST");
        empresa.setId(1L);
        convenio.setEmpresa(empresa);
        convenio.setFechaInicio(LocalDate.now().minusDays(1));
        convenio.setFechaFin(LocalDate.now().plusDays(30));
        convenio.setEstado(EstadoConvenio.VIGENTE);
        return convenio;
    }

}
