package ni.edu.uam.practicasprofesionales.model.alianzas;

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
@Table(name = "convenios")
public class Convenio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "codigo", nullable = false, unique = true, length = 150)
    private String codigo;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin", nullable = false)
    private LocalDate fechaFin;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 32)
    private EstadoConvenio estado;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCodigo() { return codigo; }
    public void setCodigo(String value) { this.codigo = value; }
    public Empresa getEmpresa() { return empresa; }
    public void setEmpresa(Empresa value) { this.empresa = value; }
    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate value) { this.fechaInicio = value; }
    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate value) { this.fechaFin = value; }
    public EstadoConvenio getEstado() { return estado; }
    public void setEstado(EstadoConvenio value) { this.estado = value; }

    @Override
    public String toString() { return codigo; }
}
