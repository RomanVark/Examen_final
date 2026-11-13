package ni.edu.uam.practicasprofesionales.model.academico;

import ni.edu.uam.practicasprofesionales.model.academico.*;
import ni.edu.uam.practicasprofesionales.model.alianzas.*;
import ni.edu.uam.practicasprofesionales.model.asignaciones.*;
import ni.edu.uam.practicasprofesionales.model.seguimiento.*;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import ni.edu.uam.practicasprofesionales.model.enums.*;

@Entity
@Table(name = "tutores_academicos")
public class TutorAcademico {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "nombres", nullable = false, unique = false, length = 150)
    private String nombres;

    @Column(name = "apellidos", nullable = false, unique = false, length = 150)
    private String apellidos;

    @Column(name = "correo", nullable = false, unique = true, length = 150)
    private String correo;

    @Column(name = "especialidad", nullable = false, unique = false, length = 150)
    private String especialidad;

    @Column(name = "activo", nullable = false)
    private Boolean activo;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombres() { return nombres; }
    public void setNombres(String value) { this.nombres = value; }
    public String getApellidos() { return apellidos; }
    public void setApellidos(String value) { this.apellidos = value; }
    public String getCorreo() { return correo; }
    public void setCorreo(String value) { this.correo = value; }
    public String getEspecialidad() { return especialidad; }
    public void setEspecialidad(String value) { this.especialidad = value; }
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean value) { this.activo = value; }

    @Override
    public String toString() { return nombres + " " + apellidos; }
}
