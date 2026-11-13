package ni.edu.uam.practicasprofesionales.service;

import jakarta.persistence.EntityManager;
import ni.edu.uam.practicasprofesionales.model.academico.*;
import ni.edu.uam.practicasprofesionales.persistence.JpaUtil;
import ni.edu.uam.practicasprofesionales.repository.JpaRepository;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/** Casos de uso académicos: CRUD, integridad y dos consultas HQL con JOIN. */
public final class AcademicoService {
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
 /** HQL 1: filtra estudiantes con JOIN a Carrera para acceder al nombre. */
 public List<Estudiante> buscarEstudiantes(String termino){
  EntityManager em=JpaUtil.open();
  try{
   return em.createQuery("select e from Estudiante e join fetch e.carrera c "+
      "where lower(e.carnet) like :busca or lower(e.nombres) like :busca "+
      "or lower(e.apellidos) like :busca or lower(c.nombre) like :busca "+
      "order by e.apellidos,e.nombres",Estudiante.class)
     .setParameter("busca","%"+termino.strip().toLowerCase(Locale.ROOT)+"%").getResultList();
  }finally{em.close();}
 }
 public List<TutorAcademico> buscarTutores(String termino){
  EntityManager em=JpaUtil.open();
  try{return em.createQuery("select t from TutorAcademico t where lower(t.nombres) like :q "+
   "or lower(t.apellidos) like :q or lower(t.correo) like :q order by t.apellidos",TutorAcademico.class)
    .setParameter("q","%"+termino.strip().toLowerCase(Locale.ROOT)+"%").getResultList();
  }finally{em.close();}
 }
 /** HQL 2: agrupación con LEFT JOIN, muestra incluso carreras sin estudiantes. */
 public List<Object[]> estudiantesPorCarrera(){
  EntityManager em=JpaUtil.open();
  try{return em.createQuery("select c.nombre, count(e.id) from Carrera c "+
    "left join c.estudiantes e group by c.id,c.nombre order by c.nombre",Object[].class).getResultList();}
  finally{em.close();}
 }
}
