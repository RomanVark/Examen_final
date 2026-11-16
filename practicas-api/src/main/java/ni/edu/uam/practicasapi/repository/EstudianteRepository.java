package ni.edu.uam.practicasapi.repository;
import ni.edu.uam.practicasapi.model.EstudianteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
public interface EstudianteRepository extends JpaRepository<EstudianteEntity, Long> {}
