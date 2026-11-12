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
@Table(name = "practicas", uniqueConstraints = @UniqueConstraint(name = "uq_practicas_par", columnNames = {"estudiante_id", "oferta_id"}))
public class Practica {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "estudiante_id", nullable = false)
    private Estudiante estudiante;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "oferta_id", nullable = false)
    private OfertaPractica oferta;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "tutor_academico_id", nullable = false)
    private TutorAcademico tutorAcademico;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "supervisor_empresa_id", nullable = false)
    private SupervisorEmpresa supervisorEmpresa;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin", nullable = false)
    private LocalDate fechaFin;

    @Column(name = "horas_requeridas", nullable = false)
    private Integer horasRequeridas;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 32)
    private EstadoPractica estado;

    @OneToMany(mappedBy = "practica", fetch = FetchType.LAZY)
    private List<Bitacora> bitacoras = new ArrayList<>();

    @OneToMany(mappedBy = "practica", fetch = FetchType.LAZY)
    private List<Evaluacion> evaluaciones = new ArrayList<>();

    @OneToMany(mappedBy = "practica", fetch = FetchType.LAZY)
    private List<Documento> documentos = new ArrayList<>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Estudiante getEstudiante() { return estudiante; }
    public void setEstudiante(Estudiante value) { this.estudiante = value; }
    public OfertaPractica getOferta() { return oferta; }
    public void setOferta(OfertaPractica value) { this.oferta = value; }
    public TutorAcademico getTutorAcademico() { return tutorAcademico; }
    public void setTutorAcademico(TutorAcademico value) { this.tutorAcademico = value; }
    public SupervisorEmpresa getSupervisorEmpresa() { return supervisorEmpresa; }
    public void setSupervisorEmpresa(SupervisorEmpresa value) { this.supervisorEmpresa = value; }
    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate value) { this.fechaInicio = value; }
    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate value) { this.fechaFin = value; }
    public Integer getHorasRequeridas() { return horasRequeridas; }
    public void setHorasRequeridas(Integer value) { this.horasRequeridas = value; }
    public EstadoPractica getEstado() { return estado; }
    public void setEstado(EstadoPractica value) { this.estado = value; }
    public List<Bitacora> getBitacoras() { return bitacoras; }
    public List<Evaluacion> getEvaluaciones() { return evaluaciones; }
    public List<Documento> getDocumentos() { return documentos; }

    @Override
    public String toString() { return "Práctica #" + id; }
}
