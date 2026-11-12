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
@Table(name = "bitacoras")
public class Bitacora {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "practica_id", nullable = false)
    private Practica practica;

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @Column(name = "actividad", nullable = false, unique = false, length = 4000)
    private String actividad;

    @Column(name = "horas", nullable = false)
    private Integer horas;

    @Column(name = "validada", nullable = false)
    private Boolean validada;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Practica getPractica() { return practica; }
    public void setPractica(Practica value) { this.practica = value; }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate value) { this.fecha = value; }
    public String getActividad() { return actividad; }
    public void setActividad(String value) { this.actividad = value; }
    public Integer getHoras() { return horas; }
    public void setHoras(Integer value) { this.horas = value; }
    public Boolean getValidada() { return validada; }
    public void setValidada(Boolean value) { this.validada = value; }

    @Override
    public String toString() { return "Bitácora #" + id; }
}
