package ni.edu.uam.practicasapi.model;
import jakarta.persistence.*;

@Entity
@Table(name = "estudiantes")
public class EstudianteEntity {
    @Id private Long id;
    @Column(nullable = false, length = 150) private String carnet;
    @Column(nullable = false, length = 150) private String nombres;
    @Column(nullable = false, length = 150) private String apellidos;
    @Column(name = "carrera_id", nullable = false) private Long carreraId;
    @Column(nullable = false) private Boolean activo;
    protected EstudianteEntity() {}
    public Long getId() { return id; }
    public String getCarnet() { return carnet; }
    public String getNombres() { return nombres; }
    public String getApellidos() { return apellidos; }
    public Long getCarreraId() { return carreraId; }
    public Boolean getActivo() { return activo; }
}
