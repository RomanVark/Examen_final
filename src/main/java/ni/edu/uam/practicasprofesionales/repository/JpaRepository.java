package ni.edu.uam.practicasprofesionales.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import ni.edu.uam.practicasprofesionales.persistence.JpaUtil;
import java.util.List;

/** Repositorio genérico transaccional; todas las operaciones CRUD se ejecutan con JPA/Hibernate. */
public class JpaRepository<T> {
    private final Class<T> type;
    public JpaRepository(Class<T> type) { this.type = type; }

    public List<T> findAll() {
        try (EntityManager em = JpaUtil.open()) {
            return em.createQuery("select e from " + type.getSimpleName() + " e order by e.id", type)
                     .getResultList();
        }
    }

    public T findById(Long id) {
        try (EntityManager em = JpaUtil.open()) { return em.find(type, id); }
    }

    public T save(T entity) {
        ValidacionDeNegocio.validar(entity);
        try (EntityManager em = JpaUtil.open()) {
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();
                ValidacionDeNegocio.validarUnicidad(em, entity);
                Object id = type.getMethod("getId").invoke(entity);
                T result;
                if (id == null) { em.persist(entity); result = entity; }
                else result = em.merge(entity);
                tx.commit();
                return result;
            } catch (Exception ex) {
                if (tx.isActive()) tx.rollback();
                throw new IllegalStateException("No fue posible guardar. Verifique campos únicos y relaciones. Detalle: " + ex.getMessage(), ex);
            }
        }
    }

    public void delete(Long id) {
        try (EntityManager em = JpaUtil.open()) {
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();
                T entity = em.find(type, id);
                if (entity == null) throw new IllegalArgumentException("El registro ya no existe.");
                em.remove(entity);
                tx.commit();
            } catch (RuntimeException ex) {
                if (tx.isActive()) tx.rollback();
                throw new IllegalStateException("No se puede eliminar el registro. Puede estar asociado a otros datos. " + ex.getMessage(), ex);
            }
        }
    }

    public long count() {
        try (EntityManager em = JpaUtil.open()) {
            return em.createQuery("select count(e) from " + type.getSimpleName() + " e", Long.class).getSingleResult();
        }
    }
}
