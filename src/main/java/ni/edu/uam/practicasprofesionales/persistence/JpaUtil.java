package ni.edu.uam.practicasprofesionales.persistence;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

/** Adaptador de compatibilidad: JPA y Hibernate nativo comparten la misma SessionFactory. */
public final class JpaUtil {
    private JpaUtil() { }

    public static EntityManagerFactory factory() {
        return HibernateUtil.getSessionFactory();
    }

    public static EntityManager open() {
        return HibernateUtil.openSession();
    }

    public static void shutdown() {
        HibernateUtil.cerrar();
    }
}
