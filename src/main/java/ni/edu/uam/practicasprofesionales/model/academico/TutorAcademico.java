package ni.edu.uam.practicasprofesionales.model.academico;

import jakarta.persistence.*;

@Entity @Table(name="tutores_academicos")
public class TutorAcademico {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false,length=150) private String nombres;
 @Column(nullable=false,length=150) private String apellidos;
 @Column(nullable=false,unique=true,length=150) private String correo;
 @Column(nullable=false,length=150) private String especialidad;
 @Column(nullable=false) private boolean activo=true;
 public Long getId(){return id;}
 public void setId(Long id){this.id=id;}
 public String getNombres(){return nombres;}
 public void setNombres(String nombres){this.nombres=nombres;}
 public String getApellidos(){return apellidos;}
 public void setApellidos(String apellidos){this.apellidos=apellidos;}
 public String getCorreo(){return correo;}
 public void setCorreo(String correo){this.correo=correo;}
 public String getEspecialidad(){return especialidad;}
 public void setEspecialidad(String especialidad){this.especialidad=especialidad;}
 public boolean isActivo(){return activo;}
 public void setActivo(boolean activo){this.activo=activo;}
 @Override public String toString(){return nombres+" "+apellidos;}
}
