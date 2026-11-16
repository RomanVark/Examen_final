package ni.edu.uam.practicasapi.model;
import jakarta.persistence.*;

@Entity
@Table(name = "empresas")
public class EmpresaEntity {
    @Id private Long id;
    @Column(name = "razon_social", nullable = false, length = 150) private String razonSocial;
    @Column(nullable = false, length = 150) private String sector;
    @Column(nullable = false) private Boolean activa;
    protected EmpresaEntity() {}
    public Long getId() { return id; }
    public String getRazonSocial() { return razonSocial; }
    public String getSector() { return sector; }
    public Boolean getActiva() { return activa; }
}
