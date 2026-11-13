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
@Table(name = "estudiantes")
public class Estudiante {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "carnet", nullable = false, unique = true, length = 150)
    private String carnet;

    @Column(name = "nombres", nullable = false, unique = false, length = 150)
    private String nombres;

    @Column(name = "apellidos", nullable = false, unique = false, length = 150)
    private String apellidos;

    @Column(name = "correo", nullable = false, unique = true, length = 150)
    private String correo;

    @Column(name = "telefono", nullable = true, unique = false, length = 150)
    private String telefono;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "carrera_id", nullable = false)
    private Carrera carrera;

    @Column(name = "activo", nullable = false)
    private Boolean activo;

    @OneToMany(mappedBy = "estudiante", fetch = FetchType.LAZY)
    private List<Postulacion> postulaciones = new ArrayList<>();

    @OneToMany(mappedBy = "estudiante", fetch = FetchType.LAZY)
    private List<Practica> practicas = new ArrayList<>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCarnet() { return carnet; }
    public void setCarnet(String value) { this.carnet = value; }
    public String getNombres() { return nombres; }
    public void setNombres(String value) { this.nombres = value; }
    public String getApellidos() { return apellidos; }
    public void setApellidos(String value) { this.apellidos = value; }
    public String getCorreo() { return correo; }
    public void setCorreo(String value) { this.correo = value; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String value) { this.telefono = value; }
    public Carrera getCarrera() { return carrera; }
    public void setCarrera(Carrera value) { this.carrera = value; }
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean value) { this.activo = value; }
    public List<Postulacion> getPostulaciones() { return postulaciones; }
    public List<Practica> getPracticas() { return practicas; }

    @Override
    public String toString() { return carnet + " · " + nombres + " " + apellidos; }
}
