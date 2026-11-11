package ni.edu.uam.practicasprofesionales.service;

import ni.edu.uam.practicasprofesionales.model.academico.*;
import ni.edu.uam.practicasprofesionales.persistence.JpaUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assumptions;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

/** Pruebas reales de CRUD contra PostgreSQL; omite cuando DB_PASSWORD no está definido. */
class AcademicoIntegracionTest {
 private AcademicoService servicio=new AcademicoService();
 private void requireDb(){Assumptions.assumeTrue(System.getenv("DB_PASSWORD")!=null,"Configure la base de datos local.");}
 @Test void crudCarreraYEstudiante(){
  requireDb();
  String tag=UUID.randomUUID().toString().substring(0,10).toUpperCase();
  Carrera c=new Carrera();c.setCodigo("T"+tag);c.setNombre("Carrera de prueba");
  c.setFacultad("Pruebas");
  Carrera guardada=null;Estudiante estudiante=null;
  try{
   guardada=servicio.guardar(c);
   assertNotNull(guardada.getId());
   guardada.setNombre("Carrera editada");
   guardada=servicio.guardar(guardada);
   assertTrue(servicio.carreras().stream().anyMatch(x->x.getId().equals(guardada.getId())&&x.getNombre().equals("Carrera editada")));
   Estudiante e=new Estudiante();e.setCarnet("T-"+tag);e.setNombres("Persona");
   e.setApellidos("Prueba");e.setCorreo("t"+tag.toLowerCase()+"@demo.example");e.setCarrera(guardada);
   estudiante=servicio.guardar(e);
   assertNotNull(estudiante.getId());
   assertTrue(servicio.buscarEstudiantes("Carrera editada").stream().anyMatch(x->x.getId().equals(estudiante.getId())));
  }finally{
   if(estudiante!=null)servicio.eliminarEstudiante(estudiante.getId());
   if(guardada!=null)servicio.eliminarCarrera(guardada.getId());
   JpaUtil.close();
  }
 }
 @Test void crudTutorYCorreoUnico(){
  requireDb();
  String tag=UUID.randomUUID().toString().substring(0,10).toLowerCase();
  TutorAcademico t=new TutorAcademico();t.setNombres("Tutor");
  t.setApellidos("Prueba");t.setCorreo("tutor-"+tag+"@demo.example");t.setEspecialidad("Ingeniería");
  TutorAcademico guardado=null;
  try{
   guardado=servicio.guardar(t);assertNotNull(guardado.getId());
   guardado.setEspecialidad("Gestión");guardado=servicio.guardar(guardado);
   assertTrue(servicio.buscarTutores("tutor-"+tag).stream().anyMatch(x->x.getEspecialidad().equals("Gestión")));
   TutorAcademico duplicado=new TutorAcademico();duplicado.setNombres("Otro");
   duplicado.setApellidos("Tutor");duplicado.setCorreo(t.getCorreo());duplicado.setEspecialidad("Pruebas");
   assertThrows(IllegalArgumentException.class,()->servicio.guardar(duplicado));
  }finally{
   if(guardado!=null)servicio.eliminarTutor(guardado.getId());
   JpaUtil.close();
  }
 }
}
