package ni.edu.uam.practicasapi.repository;
import java.time.Instant;
import java.util.List;
import ni.edu.uam.practicasapi.model.ConsumoApi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ConsumoApiRepository extends JpaRepository<ConsumoApi, Long> {
    @Query(value = """
        SELECT metodo, ruta, COUNT(*) AS total,
               SUM(CASE WHEN codigo_http < 400 THEN 1 ELSE 0 END) AS exitosas,
               SUM(CASE WHEN codigo_http >= 400 THEN 1 ELSE 0 END) AS errores,
               ROUND(AVG(duracion_ms)::numeric, 2) AS promedio_ms
        FROM api_consumos WHERE instante >= :desde
        GROUP BY metodo, ruta ORDER BY total DESC
        """, nativeQuery = true)
    List<Object[]> resumirDesde(@Param("desde") Instant desde);
}
