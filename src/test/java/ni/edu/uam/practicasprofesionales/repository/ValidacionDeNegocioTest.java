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
