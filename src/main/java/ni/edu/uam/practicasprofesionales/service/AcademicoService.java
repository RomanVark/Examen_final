package ni.edu.uam.practicasprofesionales.service;

import ni.edu.uam.practicasprofesionales.model.academico.*;
import ni.edu.uam.practicasprofesionales.repository.JpaRepository;
import ni.edu.uam.practicasprofesionales.repository.AcademicoDao;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/** Casos de uso académicos: CRUD, integridad y dos consultas HQL con JOIN. */
public final class AcademicoService {
 private final AcademicoDao consultas = new AcademicoDao();
 private final JpaRepository<Carrera> carreraRepo = new JpaRepository<>(Carrera.class);
 private final JpaRepository<Estudiante> estudianteRepo = new JpaRepository<>(Estudiante.class);
 private final JpaRepository<TutorAcademico> tutorRepo = new JpaRepository<>(TutorAcademico.class);
 public List<Carrera> carreras(){return carreraRepo.findAll();}
 public List<Estudiante> estudiantes(){return estudianteRepo.findAll();}
 public List<TutorAcademico> tutores(){return tutorRepo.findAll();}
 public Carrera guardar(Carrera c){
  AcademicoValidator.validarCarrera(c);
  if(carreras().stream().anyMatch(x->!Objects.equals(x.getId(),c.getId())&&x.getCodigo().equalsIgnoreCase(c.getCodigo())))
   throw new IllegalArgumentException("El código de carrera ya existe.");
  return carreraRepo.save(c);
 }
 public Estudiante guardar(Estudiante e){
  AcademicoValidator.validarEstudiante(e);
  if(estudiantes().stream().anyMatch(x->!Objects.equals(x.getId(),e.getId())&&x.getCarnet().equalsIgnoreCase(e.getCarnet())))
   throw new IllegalArgumentException("El carné ya está registrado.");
  if(estudiantes().stream().anyMatch(x->!Objects.equals(x.getId(),e.getId())&&x.getCorreo().equalsIgnoreCase(e.getCorreo())))
   throw new IllegalArgumentException("El correo del estudiante ya existe.");
  return estudianteRepo.save(e);
 }
 public TutorAcademico guardar(TutorAcademico t){
  AcademicoValidator.validarTutor(t);
  if(tutores().stream().anyMatch(x->!Objects.equals(x.getId(),t.getId())&&x.getCorreo().equalsIgnoreCase(t.getCorreo())))
   throw new IllegalArgumentException("El correo del tutor ya existe.");
  return tutorRepo.save(t);
 }
 public void eliminarCarrera(Long id){carreraRepo.delete(id);}
 public void eliminarEstudiante(Long id){estudianteRepo.delete(id);}
 public void eliminarTutor(Long id){tutorRepo.delete(id);}
 /** Consulta HQL con parámetro y JOIN FETCH, ejecutada por el DAO nativo. */
 public List<Estudiante> buscarEstudiantes(String termino) { return consultas.buscarEstudiantes(termino); }
 public List<TutorAcademico> buscarTutores(String termino) { return consultas.buscarTutores(termino); }
 /** Consulta HQL de agregación con GROUP BY. */
 public List<Object[]> estudiantesPorCarrera() { return consultas.estudiantesPorCarrera(); }
}
