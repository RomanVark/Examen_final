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
@Table(name = "empresas")
public class Empresa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "ruc", nullable = false, unique = true, length = 150)
    private String ruc;

    @Column(name = "razon_social", nullable = false, unique = false, length = 150)
    private String razonSocial;

    @Column(name = "sector", nullable = false, unique = false, length = 150)
    private String sector;

    @Column(name = "correo", nullable = false, unique = false, length = 150)
    private String correo;

    @Column(name = "telefono", nullable = true, unique = false, length = 150)
    private String telefono;

    @Column(name = "direccion", nullable = true, unique = false, length = 500)
    private String direccion;

    @Column(name = "activa", nullable = false)
    private Boolean activa;

    @OneToMany(mappedBy = "empresa", fetch = FetchType.LAZY)
    private List<SupervisorEmpresa> supervisores = new ArrayList<>();

    @OneToMany(mappedBy = "empresa", fetch = FetchType.LAZY)
    private List<Convenio> convenios = new ArrayList<>();

    @OneToMany(mappedBy = "empresa", fetch = FetchType.LAZY)
    private List<OfertaPractica> ofertas = new ArrayList<>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getRuc() { return ruc; }
    public void setRuc(String value) { this.ruc = value; }
    public String getRazonSocial() { return razonSocial; }
    public void setRazonSocial(String value) { this.razonSocial = value; }
    public String getSector() { return sector; }
    public void setSector(String value) { this.sector = value; }
    public String getCorreo() { return correo; }
    public void setCorreo(String value) { this.correo = value; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String value) { this.telefono = value; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String value) { this.direccion = value; }
    public Boolean getActiva() { return activa; }
    public void setActiva(Boolean value) { this.activa = value; }
    public List<SupervisorEmpresa> getSupervisores() { return supervisores; }
    public List<Convenio> getConvenios() { return convenios; }
    public List<OfertaPractica> getOfertas() { return ofertas; }

    @Override
    public String toString() { return razonSocial + " (" + ruc + ")"; }
}
