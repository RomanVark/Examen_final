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
@Table(name = "carreras")
public class Carrera {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "codigo", nullable = false, unique = true, length = 150)
    private String codigo;

    @Column(name = "nombre", nullable = false, unique = false, length = 150)
    private String nombre;

    @Column(name = "facultad", nullable = false, unique = false, length = 150)
    private String facultad;

    @Column(name = "activa", nullable = false)
    private Boolean activa;

    @OneToMany(mappedBy = "carrera", fetch = FetchType.LAZY)
    private List<Estudiante> estudiantes = new ArrayList<>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCodigo() { return codigo; }
    public void setCodigo(String value) { this.codigo = value; }
    public String getNombre() { return nombre; }
    public void setNombre(String value) { this.nombre = value; }
    public String getFacultad() { return facultad; }
    public void setFacultad(String value) { this.facultad = value; }
    public Boolean getActiva() { return activa; }
    public void setActiva(Boolean value) { this.activa = value; }
    public List<Estudiante> getEstudiantes() { return estudiantes; }

    @Override
    public String toString() { return codigo + " · " + nombre; }
}
