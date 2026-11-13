package ni.edu.uam.practicasprofesionales.repository;

import ni.edu.uam.practicasprofesionales.model.academico.Estudiante;
import ni.edu.uam.practicasprofesionales.model.academico.TutorAcademico;
import ni.edu.uam.practicasprofesionales.model.alianzas.Empresa;
import ni.edu.uam.practicasprofesionales.model.alianzas.SupervisorEmpresa;
import ni.edu.uam.practicasprofesionales.model.asignaciones.OfertaPractica;
import ni.edu.uam.practicasprofesionales.model.asignaciones.Postulacion;
import ni.edu.uam.practicasprofesionales.model.asignaciones.Practica;
import ni.edu.uam.practicasprofesionales.model.enums.EstadoOferta;
import ni.edu.uam.practicasprofesionales.model.enums.EstadoPostulacion;
import ni.edu.uam.practicasprofesionales.model.enums.EstadoPractica;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class AsignacionesValidacionTest {

    @Test
    void ofertaConCuposCeroSeRechaza() {
        OfertaPractica oferta = ofertaValida();
        oferta.setCupos(0);
        assertThrows(IllegalArgumentException.class, () -> ValidacionDeNegocio.validar(oferta));
    }

    @Test
    void ofertaConFechasInvertidasSeRechaza() {
        OfertaPractica oferta = ofertaValida();
        oferta.setFechaInicio(LocalDate.of(2026, 12, 20));
        oferta.setFechaFin(LocalDate.of(2026, 12, 1));
        assertThrows(IllegalArgumentException.class, () -> ValidacionDeNegocio.validar(oferta));
    }

    @Test
    void ofertaSinEmpresaRegistradaSeRechaza() {
        OfertaPractica oferta = ofertaValida();
        oferta.setEmpresa(new Empresa());
        assertThrows(IllegalArgumentException.class, () -> ValidacionDeNegocio.validar(oferta));
    }

    @Test
    void postulacionAOfertaCerradaSeRechaza() {
        Postulacion postulacion = postulacionValida();
        postulacion.getOferta().setEstado(EstadoOferta.CERRADA);
        assertThrows(IllegalArgumentException.class, () -> ValidacionDeNegocio.validar(postulacion));
    }

    @Test
    void postulacionSinEstudianteRegistradoSeRechaza() {
        Postulacion postulacion = postulacionValida();
        postulacion.setEstudiante(new Estudiante());
        assertThrows(IllegalArgumentException.class, () -> ValidacionDeNegocio.validar(postulacion));
    }

    @Test
    void practicaConSupervisorDeOtraEmpresaSeRechaza() {
        Practica practica = practicaValida();
        Empresa otraEmpresa = new Empresa();
        otraEmpresa.setId(22L);
        SupervisorEmpresa supervisor = practica.getSupervisorEmpresa();
        supervisor.setEmpresa(otraEmpresa);
        assertThrows(IllegalArgumentException.class, () -> ValidacionDeNegocio.validar(practica));
    }

    @Test
    void practicaConSupervisorInactivoSeRechaza() {
        Practica practica = practicaValida();
        practica.getSupervisorEmpresa().setActivo(false);
        assertThrows(IllegalArgumentException.class, () -> ValidacionDeNegocio.validar(practica));
    }

    @Test
    void practicaConHorasCeroSeRechaza() {
        Practica practica = practicaValida();
        practica.setHorasRequeridas(0);
        assertThrows(IllegalArgumentException.class, () -> ValidacionDeNegocio.validar(practica));
    }

    @Test
    void practicaDentroDeFechasYConSupervisorCorrectoSeAcepta() {
        assertDoesNotThrow(() -> ValidacionDeNegocio.validar(practicaValida()));
    }

    private static OfertaPractica ofertaValida() {
        Empresa empresa = new Empresa();
        empresa.setId(10L);
        OfertaPractica oferta = new OfertaPractica();
        oferta.setId(30L);
        oferta.setCodigo("OF-TEST");
        oferta.setTitulo("Práctica de prueba");
        oferta.setEmpresa(empresa);
        oferta.setCupos(2);
        oferta.setFechaInicio(LocalDate.now().plusDays(1));
        oferta.setFechaFin(LocalDate.now().plusDays(60));
        oferta.setEstado(EstadoOferta.ABIERTA);
        return oferta;
    }

    private static Postulacion postulacionValida() {
        Estudiante estudiante = new Estudiante();
        estudiante.setId(5L);
        Postulacion postulacion = new Postulacion();
        postulacion.setEstudiante(estudiante);
        postulacion.setOferta(ofertaValida());
        postulacion.setFecha(LocalDate.now());
        postulacion.setEstado(EstadoPostulacion.PENDIENTE);
        return postulacion;
    }

    private static Practica practicaValida() {
        Empresa empresa = new Empresa();
        empresa.setId(10L);
        OfertaPractica oferta = ofertaValida();

        Estudiante estudiante = new Estudiante();
        estudiante.setId(5L);
        TutorAcademico tutor = new TutorAcademico();
        tutor.setId(7L);
        SupervisorEmpresa supervisor = new SupervisorEmpresa();
        supervisor.setId(8L);
        supervisor.setEmpresa(empresa);
        supervisor.setActivo(true);

        Practica practica = new Practica();
        practica.setEstudiante(estudiante);
        practica.setOferta(oferta);
        practica.setTutorAcademico(tutor);
        practica.setSupervisorEmpresa(supervisor);
        practica.setFechaInicio(oferta.getFechaInicio());
        practica.setFechaFin(oferta.getFechaFin());
        practica.setHorasRequeridas(240);
        practica.setEstado(EstadoPractica.PLANIFICADA);
        return practica;
    }
}
