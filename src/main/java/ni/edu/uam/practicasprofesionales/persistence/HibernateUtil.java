package ni.edu.uam.practicasprofesionales.persistence;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.Locale;
import java.util.Set;

/** Único punto de arranque de Hibernate nativo para todo JavaFX. */
public final class HibernateUtil {
    private static volatile SessionFactory factory;
    private static final Set<String> DDL_SEGUROS = Set.of("validate", "none", "update");

    private HibernateUtil() { }

    public static SessionFactory getSessionFactory() {
        SessionFactory actual = factory;
        if (actual == null || actual.isClosed()) {
            synchronized (HibernateUtil.class) {
                actual = factory;
                if (actual == null || actual.isClosed()) {
                    factory = crear();
                    actual = factory;
                }
            }
        }
        return actual;
    }

    private static SessionFactory crear() {
        String password = System.getenv("DB_PASSWORD");
        if (password == null || password.isBlank()) {
            throw new IllegalStateException(
                "Falta DB_PASSWORD: configura las variables de entorno de PostgreSQL.");
        }
        String ddl = variable("HIBERNATE_DDL_AUTO", "validate").toLowerCase(Locale.ROOT);
        if (!DDL_SEGUROS.contains(ddl)) {
            throw new IllegalArgumentException(
                "HIBERNATE_DDL_AUTO debe ser validate, none o update.");
        }
        try {
            Configuration cfg = new Configuration().configure("hibernate.cfg.xml");
            cfg.setProperty("hibernate.connection.url",
                variable("DB_URL", "jdbc:postgresql://localhost:5432/practicas_profesionales"));
            cfg.setProperty("hibernate.connection.username", variable("DB_USER", "postgres"));
            cfg.setProperty("hibernate.connection.password", password);
            cfg.setProperty("hibernate.hbm2ddl.auto", ddl);
            cfg.setProperty("hibernate.show_sql", variable("HIBERNATE_SHOW_SQL", "false"));
            return cfg.buildSessionFactory();
        } catch (RuntimeException ex) {
            throw new IllegalStateException(
                "No se pudo iniciar Hibernate nativo. Comprueba PostgreSQL, credenciales y esquema. "
                + ex.getMessage(), ex);
        }
    }

    private static String variable(String nombre, String defecto) {
        String valor = System.getenv(nombre);
        return valor == null || valor.isBlank() ? defecto : valor;
    }

    public static Session openSession() {
        return getSessionFactory().openSession();
    }

    public static synchronized void cerrar() {
        if (factory != null) {
            if (!factory.isClosed()) factory.close();
            factory = null;
        }
    }
}
