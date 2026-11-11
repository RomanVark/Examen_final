package ni.edu.uam.practicasprofesionales.service;

import ni.edu.uam.practicasprofesionales.model.academico.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AcademicoValidatorTest {
 private Carrera carrera(){
  Carrera c=new Carrera();c.setId(1L);c.setCodigo("SIS");c.setNombre("Sistemas");
  c.setFacultad("Tecnología");return c;
 }
 private Estudiante estudiante(){
  Estudiante e=new Estudiante();e.setCarnet("2026-001");e.setNombres("Ana");
  e.setApellidos("López");e.setCorreo("ana@demo.example");e.setCarrera(carrera());return e;
 }
 @Test void rechazaCarnetInvalido(){
  Estudiante e=estudiante();e.setCarnet("@");
  assertThrows(IllegalArgumentException.class,()->AcademicoValidator.validarEstudiante(e));
 }
 @Test void rechazaCorreoInvalido(){
  Estudiante e=estudiante();e.setCorreo("correo-sin-arroba");
  assertThrows(IllegalArgumentException.class,()->AcademicoValidator.validarEstudiante(e));
 }
 @Test void rechazaCamposObligatoriosYRelacion(){
  Carrera c=carrera();c.setNombre("   ");
  assertThrows(IllegalArgumentException.class,()->AcademicoValidator.validarCarrera(c));
  Estudiante e=estudiante();e.setCarrera(null);
  assertThrows(IllegalArgumentException.class,()->AcademicoValidator.validarEstudiante(e));
  TutorAcademico t=new TutorAcademico();t.setNombres("");t.setApellidos("A");
  t.setCorreo("a@demo.example");t.setEspecialidad("Sistemas");
  assertThrows(IllegalArgumentException.class,()->AcademicoValidator.validarTutor(t));
 }
 @Test void normalizaCorreosYCodigos(){
  Estudiante e=estudiante();e.setCorreo("ANA@DEMO.EXAMPLE");
  AcademicoValidator.validarEstudiante(e);assertEquals("ana@demo.example",e.getCorreo());
  Carrera c=carrera();c.setCodigo("sis");
  AcademicoValidator.validarCarrera(c);assertEquals("SIS",c.getCodigo());
 }
 @Test void rechazaCarreraInactivaParaEstudianteActivo(){
  Estudiante e=estudiante();e.getCarrera().setActiva(false);
  assertThrows(IllegalArgumentException.class,()->AcademicoValidator.validarEstudiante(e));
 }
}
