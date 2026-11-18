package ni.edu.uam.practicasprofesionales.repository;

/** Mantiene las llamadas JavaFX existentes y utiliza ahora el DAO nativo de Hibernate. */
public class JpaRepository<T> extends GenericDao<T> {
    public JpaRepository(Class<T> type) {
        super(type);
    }
}
