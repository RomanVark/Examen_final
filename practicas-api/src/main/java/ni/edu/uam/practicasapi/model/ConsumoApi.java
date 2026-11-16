package ni.edu.uam.practicasapi.model;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "api_consumos")
public class ConsumoApi {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false) private Instant instante;
    @Column(nullable = false, length = 10) private String metodo;
    @Column(nullable = false, length = 200) private String ruta;
    @Column(name = "codigo_http", nullable = false) private Integer codigoHttp;
    @Column(name = "duracion_ms", nullable = false) private Long duracionMs;
    protected ConsumoApi() {}
    public ConsumoApi(String metodo, String ruta, int codigoHttp, long duracionMs) {
        this.instante = Instant.now();
        this.metodo = metodo;
        this.ruta = ruta;
        this.codigoHttp = codigoHttp;
        this.duracionMs = duracionMs;
    }
}
