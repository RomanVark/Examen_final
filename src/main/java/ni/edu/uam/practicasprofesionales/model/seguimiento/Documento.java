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
@Table(name = "documentos")
public class Documento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "practica_id", nullable = false)
    private Practica practica;

    @Column(name = "tipo", nullable = false, unique = false, length = 150)
    private String tipo;

    @Column(name = "nombre_archivo", nullable = false, unique = false, length = 150)
    private String nombreArchivo;

    @Column(name = "ruta_archivo", nullable = false, unique = false, length = 500)
    private String rutaArchivo;

    @Column(name = "fecha_entrega", nullable = false)
    private LocalDate fechaEntrega;

    @Column(name = "verificado", nullable = false)
    private Boolean verificado;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Practica getPractica() { return practica; }
    public void setPractica(Practica value) { this.practica = value; }
    public String getTipo() { return tipo; }
    public void setTipo(String value) { this.tipo = value; }
    public String getNombreArchivo() { return nombreArchivo; }
    public void setNombreArchivo(String value) { this.nombreArchivo = value; }
    public String getRutaArchivo() { return rutaArchivo; }
    public void setRutaArchivo(String value) { this.rutaArchivo = value; }
    public LocalDate getFechaEntrega() { return fechaEntrega; }
    public void setFechaEntrega(LocalDate value) { this.fechaEntrega = value; }
    public Boolean getVerificado() { return verificado; }
    public void setVerificado(Boolean value) { this.verificado = value; }

    @Override
    public String toString() { return nombreArchivo; }
}
