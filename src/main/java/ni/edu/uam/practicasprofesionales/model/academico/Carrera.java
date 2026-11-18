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
import java.util.HashSet;
import java.util.Set;
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

    // Relación N-M: una carrera puede tener varios tutores y un tutor atender varias carreras.
    @ManyToMany
    @JoinTable(name = "carrera_tutor",
        joinColumns = @JoinColumn(name = "carrera_id"),
        inverseJoinColumns = @JoinColumn(name = "tutor_id"))
    private Set<TutorAcademico> tutores = new HashSet<>();

    /** Mantiene ambos lados de la relación bidireccional 1-N. */
    public void agregarEstudiante(Estudiante estudiante) {
        if (estudiante == null) throw new IllegalArgumentException("El estudiante es obligatorio.");
        if (estudiante.getCarrera() != null && estudiante.getCarrera() != this) {
            estudiante.getCarrera().getEstudiantes().remove(estudiante);
        }
        if (!estudiantes.contains(estudiante)) estudiantes.add(estudiante);
        estudiante.setCarrera(this);
    }

    public void quitarEstudiante(Estudiante estudiante) {
        if (estudiante != null && estudiantes.remove(estudiante) && estudiante.getCarrera() == this) {
            estudiante.setCarrera(null);
        }
    }

    /** Eliminar un tutor solo elimina la asociación, no al tutor de la base. */
    public void agregarTutor(TutorAcademico tutor) {
        if (tutor == null) throw new IllegalArgumentException("El tutor es obligatorio.");
        if (tutores.add(tutor)) tutor.getCarreras().add(this);
    }

    public void quitarTutor(TutorAcademico tutor) {
        if (tutor != null && tutores.remove(tutor)) tutor.getCarreras().remove(this);
    }

    public Set<TutorAcademico> getTutores() { return tutores; }

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
