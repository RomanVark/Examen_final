package ni.edu.uam.practicasprofesionales.model.seguimiento;

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
@Table(name = "evaluaciones")
public class Evaluacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "practica_id", nullable = false)
    private Practica practica;

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @Column(name = "evaluador", nullable = false, unique = false, length = 150)
    private String evaluador;

    @Column(name = "calificacion", nullable = false, precision = 5, scale = 2)
    private BigDecimal calificacion;

    @Column(name = "observaciones", nullable = true, unique = false, length = 4000)
    private String observaciones;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Practica getPractica() { return practica; }
    public void setPractica(Practica value) { this.practica = value; }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate value) { this.fecha = value; }
    public String getEvaluador() { return evaluador; }
    public void setEvaluador(String value) { this.evaluador = value; }
    public BigDecimal getCalificacion() { return calificacion; }
    public void setCalificacion(BigDecimal value) { this.calificacion = value; }
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String value) { this.observaciones = value; }

    @Override
    public String toString() { return "Evaluación #" + id; }
}
