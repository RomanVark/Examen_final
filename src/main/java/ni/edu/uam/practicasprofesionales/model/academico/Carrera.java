package ni.edu.uam.practicasprofesionales.model.academico;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="carreras", uniqueConstraints=@UniqueConstraint(name="uk_carreras_codigo", columnNames="codigo"))
public class Carrera {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false,length=150,unique=true) private String codigo;
 @Column(nullable=false,length=150) private String nombre;
 @Column(nullable=false,length=150) private String facultad;
 @Column(nullable=false) private boolean activa=true;
 @OneToMany(mappedBy="carrera", fetch=FetchType.LAZY)
 private List<Estudiante> estudiantes=new ArrayList<>();
 public Long getId(){return id;}
 public void setId(Long id){this.id=id;}
 public String getCodigo(){return codigo;}
 public void setCodigo(String codigo){this.codigo=codigo;}
 public String getNombre(){return nombre;}
 public void setNombre(String nombre){this.nombre=nombre;}
 public String getFacultad(){return facultad;}
 public void setFacultad(String facultad){this.facultad=facultad;}
 public boolean isActiva(){return activa;}
 public void setActiva(boolean activa){this.activa=activa;}
 public List<Estudiante> getEstudiantes(){return estudiantes;}
 @Override public String toString(){return codigo+" · "+nombre;}
}
