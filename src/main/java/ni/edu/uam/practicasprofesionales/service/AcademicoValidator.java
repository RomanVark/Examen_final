package ni.edu.uam.practicasprofesionales.service;

import ni.edu.uam.practicasprofesionales.model.academico.*;
import java.util.regex.Pattern;

/** Validaciones puras, independientes de la GUI y la conexión a PostgreSQL. */
public final class AcademicoValidator {
 private static final Pattern EMAIL=Pattern.compile("^[A-Za-z0-9._%+\\-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
 private static final Pattern CARNET=Pattern.compile("^[A-Za-z0-9-]{4,20}$");
 private AcademicoValidator(){}
 public static String requerido(String valor,String campo){
  if(valor==null||valor.isBlank())throw new IllegalArgumentException(campo+" es obligatorio.");
  String limpio=valor.strip();
  if(limpio.length()>150)throw new IllegalArgumentException(campo+" admite máximo 150 caracteres.");
  return limpio;
 }
 public static String correo(String valor){
  String limpio=requerido(valor,"Correo").toLowerCase(java.util.Locale.ROOT);
  if(!EMAIL.matcher(limpio).matches())throw new IllegalArgumentException("Correo electrónico inválido.");
  return limpio;
 }
 public static void validarCarrera(Carrera c){
  c.setCodigo(requerido(c.getCodigo(),"Código").toUpperCase(java.util.Locale.ROOT));
  c.setNombre(requerido(c.getNombre(),"Carrera"));
  c.setFacultad(requerido(c.getFacultad(),"Facultad"));
  if(c.getCodigo().length()>20)throw new IllegalArgumentException("Código de carrera demasiado largo.");
 }
 public static void validarEstudiante(Estudiante e){
  e.setCarnet(requerido(e.getCarnet(),"Carné").toUpperCase(java.util.Locale.ROOT));
  if(!CARNET.matcher(e.getCarnet()).matches())throw new IllegalArgumentException("Carné: 4-20 letras, números o guiones.");
  e.setNombres(requerido(e.getNombres(),"Nombres"));
  e.setApellidos(requerido(e.getApellidos(),"Apellidos"));
  e.setCorreo(correo(e.getCorreo()));
  if(e.getCarrera()==null || e.getCarrera().getId()==null)throw new IllegalArgumentException("Seleccione una carrera existente.");
  if(e.getTelefono()!=null&&e.getTelefono().length()>150)throw new IllegalArgumentException("Teléfono demasiado largo.");
  if(!Boolean.TRUE.equals(e.getCarrera().getActiva())&&Boolean.TRUE.equals(e.getActivo()))throw new IllegalArgumentException("No puede activar un estudiante de carrera inactiva.");
 }
 public static void validarTutor(TutorAcademico t){
  t.setNombres(requerido(t.getNombres(),"Nombres del tutor"));
  t.setApellidos(requerido(t.getApellidos(),"Apellidos del tutor"));
  t.setCorreo(correo(t.getCorreo()));
  t.setEspecialidad(requerido(t.getEspecialidad(),"Especialidad"));
 }
}
