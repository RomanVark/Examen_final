package ni.edu.uam.practicasprofesionales.model.asignaciones;

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
@Table(name = "ofertas_practica")
public class OfertaPractica {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "codigo", nullable = false, unique = true, length = 150)
    private String codigo;

    @Column(name = "titulo", nullable = false, unique = false, length = 150)
    private String titulo;

    @Column(name = "descripcion", nullable = true, unique = false, length = 4000)
    private String descripcion;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    @Column(name = "cupos", nullable = false)
    private Integer cupos;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin", nullable = false)
    private LocalDate fechaFin;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 32)
    private EstadoOferta estado;

    @OneToMany(mappedBy = "oferta", fetch = FetchType.LAZY)
    private List<Postulacion> postulaciones = new ArrayList<>();

    @OneToMany(mappedBy = "oferta", fetch = FetchType.LAZY)
    private List<Practica> practicas = new ArrayList<>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCodigo() { return codigo; }
    public void setCodigo(String value) { this.codigo = value; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String value) { this.titulo = value; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String value) { this.descripcion = value; }
    public Empresa getEmpresa() { return empresa; }
    public void setEmpresa(Empresa value) { this.empresa = value; }
    public Integer getCupos() { return cupos; }
    public void setCupos(Integer value) { this.cupos = value; }
    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate value) { this.fechaInicio = value; }
    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate value) { this.fechaFin = value; }
    public EstadoOferta getEstado() { return estado; }
    public void setEstado(EstadoOferta value) { this.estado = value; }
    public List<Postulacion> getPostulaciones() { return postulaciones; }
    public List<Practica> getPracticas() { return practicas; }

    @Override
    public String toString() { return codigo + " · " + titulo; }
}
