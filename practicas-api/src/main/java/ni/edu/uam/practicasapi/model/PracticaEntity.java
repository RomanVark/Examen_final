package ni.edu.uam.practicasapi.model;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "practicas")
public class PracticaEntity {
    @Id private Long id;
    @Column(name = "estudiante_id", nullable = false) private Long estudianteId;
    @Column(name = "oferta_id", nullable = false) private Long ofertaId;
    @Column(name = "fecha_inicio", nullable = false) private LocalDate fechaInicio;
    @Column(name = "fecha_fin", nullable = false) private LocalDate fechaFin;
    @Column(name = "horas_requeridas", nullable = false) private Integer horasRequeridas;
    @Column(nullable = false, length = 32) private String estado;
    protected PracticaEntity() {}
    public Long getId() { return id; }
    public Long getEstudianteId() { return estudianteId; }
    public Long getOfertaId() { return ofertaId; }
    public LocalDate getFechaInicio() { return fechaInicio; }
    public LocalDate getFechaFin() { return fechaFin; }
    public Integer getHorasRequeridas() { return horasRequeridas; }
    public String getEstado() { return estado; }
}
