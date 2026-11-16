package ni.edu.uam.practicasapi.repository;
import ni.edu.uam.practicasapi.model.PracticaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
public interface PracticaRepository extends JpaRepository<PracticaEntity, Long> {}
