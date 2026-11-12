package ni.edu.uam.practicasprofesionales.model.seguimiento;

import jakarta.persistence.*;
import ni.edu.uam.practicasprofesionales.model.asignaciones.Practica;
import java.time.LocalDate;

@Entity
@Table(name = "bitacoras")
public class Bitacora {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "practica_id", nullable = false)
    private Practica practica;

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @Column(name = "actividad", nullable = false, length = 4000)
    private String actividad;

    @Column(name = "horas", nullable = false)
    private Integer horas;

    @Column(name = "validada", nullable = false)
    private Boolean validada = Boolean.FALSE;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Practica getPractica() { return practica; }
    public void setPractica(Practica practica) { this.practica = practica; }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public String getActividad() { return actividad; }
    public void setActividad(String actividad) { this.actividad = actividad; }
    public Integer getHoras() { return horas; }
    public void setHoras(Integer horas) { this.horas = horas; }
    public Boolean getValidada() { return validada; }
    public void setValidada(Boolean validada) { this.validada = validada; }

    @PrePersist
    @PreUpdate
    private void validarAntesDeGuardar() {
        ni.edu.uam.practicasprofesionales.repository.ValidacionSeguimiento.validar(this);
    }

    @Override
    public String toString() { return "Bitácora #" + id; }
}
