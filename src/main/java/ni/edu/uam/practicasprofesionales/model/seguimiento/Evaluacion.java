package ni.edu.uam.practicasprofesionales.model.seguimiento;

import jakarta.persistence.*;
import ni.edu.uam.practicasprofesionales.model.asignaciones.Practica;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "evaluaciones")
public class Evaluacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "practica_id", nullable = false)
    private Practica practica;

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @Column(name = "evaluador", nullable = false, length = 150)
    private String evaluador;

    @Column(name = "calificacion", nullable = false, precision = 5, scale = 2)
    private BigDecimal calificacion;

    @Column(name = "observaciones", length = 4000)
    private String observaciones;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Practica getPractica() { return practica; }
    public void setPractica(Practica practica) { this.practica = practica; }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public String getEvaluador() { return evaluador; }
    public void setEvaluador(String evaluador) { this.evaluador = evaluador; }
    public BigDecimal getCalificacion() { return calificacion; }
    public void setCalificacion(BigDecimal calificacion) { this.calificacion = calificacion; }
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    @PrePersist
    @PreUpdate
    private void validarAntesDeGuardar() {
        ni.edu.uam.practicasprofesionales.repository.ValidacionSeguimiento.validar(this);
    }

    @Override
    public String toString() { return "Evaluación #" + id; }
}
