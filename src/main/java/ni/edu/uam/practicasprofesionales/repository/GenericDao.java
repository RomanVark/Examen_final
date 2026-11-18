package ni.edu.uam.practicasprofesionales.repository;

import ni.edu.uam.practicasprofesionales.persistence.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;
import java.util.function.Function;

/** DAO genérico con Session y Transaction nativas, usando una sola SessionFactory. */
public class GenericDao<T> {
    private final Class<T> type;

    public GenericDao(Class<T> type) {
        this.type = type;
    }

    protected <R> R consulta(Function<Session, R> accion) {
        try (Session session = HibernateUtil.openSession()) {
            return accion.apply(session);
        }
    }

    protected <R> R enTransaccion(Function<Session, R> accion) {
        try (Session session = HibernateUtil.openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                R resultado = accion.apply(session);
                tx.commit();
                return resultado;
            } catch (RuntimeException | Error ex) {
                if (tx.isActive()) tx.rollback();
                throw ex;
            }
        }
    }

    public List<T> findAll() {
        return consulta(s -> s.createQuery(
            "select e from " + type.getSimpleName() + " e order by e.id", type).getResultList());
    }

    public T findById(Long id) {
        return consulta(s -> s.find(type, id));
    }

    public T save(T entity) {
        ValidacionDeNegocio.validar(entity);
        return enTransaccion(s -> {
            ValidacionDeNegocio.validarUnicidad(s, entity);
            if (identificador(entity) == null) {
                s.persist(entity);
                return entity;
            }
            return s.merge(entity);
        });
    }

    public void delete(Long id) {
        enTransaccion(s -> {
            T existente = s.find(type, id);
            if (existente == null) throw new IllegalArgumentException("El registro ya no existe.");
            s.remove(existente);
            return null;
        });
    }

    public long count() {
        return consulta(s -> s.createQuery(
            "select count(e) from " + type.getSimpleName() + " e", Long.class)
            .getSingleResult());
    }

    private Long identificador(T entity) {
        try {
            return (Long) type.getMethod("getId").invoke(entity);
        } catch (ReflectiveOperationException ex) {
            throw new IllegalStateException("La entidad no tiene un getId accesible.", ex);
        }
    }
}
