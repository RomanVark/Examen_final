package ni.edu.uam.practicasprofesionales.model.seguimiento;

import jakarta.persistence.*;
import ni.edu.uam.practicasprofesionales.model.asignaciones.Practica;
import java.time.LocalDate;

@Entity
@Table(name = "documentos")
public class Documento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "practica_id", nullable = false)
    private Practica practica;

    @Column(name = "tipo", nullable = false, length = 150)
    private String tipo;

    @Column(name = "nombre_archivo", nullable = false, length = 150)
    private String nombreArchivo;

    @Column(name = "ruta_archivo", nullable = false, length = 500)
    private String rutaArchivo;

    @Column(name = "fecha_entrega", nullable = false)
    private LocalDate fechaEntrega;

    @Column(name = "verificado", nullable = false)
    private Boolean verificado = Boolean.FALSE;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Practica getPractica() { return practica; }
    public void setPractica(Practica practica) { this.practica = practica; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getNombreArchivo() { return nombreArchivo; }
    public void setNombreArchivo(String nombreArchivo) { this.nombreArchivo = nombreArchivo; }
    public String getRutaArchivo() { return rutaArchivo; }
    public void setRutaArchivo(String rutaArchivo) { this.rutaArchivo = rutaArchivo; }
    public LocalDate getFechaEntrega() { return fechaEntrega; }
    public void setFechaEntrega(LocalDate fechaEntrega) { this.fechaEntrega = fechaEntrega; }
    public Boolean getVerificado() { return verificado; }
    public void setVerificado(Boolean verificado) { this.verificado = verificado; }

    @PrePersist
    @PreUpdate
    private void validarAntesDeGuardar() {
        ni.edu.uam.practicasprofesionales.repository.ValidacionSeguimiento.validar(this);
    }

    @Override
    public String toString() { return nombreArchivo == null ? "Documento" : nombreArchivo; }
}
