package ni.edu.uam.practicasprofesionales.model.academico;

import jakarta.persistence.*;

@Entity
@Table(name="estudiantes")
public class Estudiante {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false,unique=true,length=150) private String carnet;
 @Column(nullable=false,length=150) private String nombres;
 @Column(nullable=false,length=150) private String apellidos;
 @Column(nullable=false,unique=true,length=150) private String correo;
 @Column(length=150) private String telefono;
 @ManyToOne(fetch=FetchType.EAGER, optional=false)
 @JoinColumn(name="carrera_id",nullable=false) private Carrera carrera;
 @Column(nullable=false) private boolean activo=true;
 public Long getId(){return id;}
 public void setId(Long id){this.id=id;}
 public String getCarnet(){return carnet;}
 public void setCarnet(String carnet){this.carnet=carnet;}
 public String getNombres(){return nombres;}
 public void setNombres(String nombres){this.nombres=nombres;}
 public String getApellidos(){return apellidos;}
 public void setApellidos(String apellidos){this.apellidos=apellidos;}
 public String getCorreo(){return correo;}
 public void setCorreo(String correo){this.correo=correo;}
 public String getTelefono(){return telefono;}
 public void setTelefono(String telefono){this.telefono=telefono;}
 public Carrera getCarrera(){return carrera;}
 public void setCarrera(Carrera carrera){this.carrera=carrera;}
 public boolean isActivo(){return activo;}
 public void setActivo(boolean activo){this.activo=activo;}
 @Override public String toString(){return carnet+" · "+nombres+" "+apellidos;}
}
