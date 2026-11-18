package ni.edu.uam.practicasprofesionales.persistence;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Punto único de inicialización de JPA con Hibernate ORM 7.
 * La configuración sensible se lee desde variables de entorno.
 */
public final class JpaUtil {
    private static volatile EntityManagerFactory factory;
    private static final Set<String> DDL_MODES = Set.of("validate", "none", "update");

    private JpaUtil() { }

    public static EntityManagerFactory factory() {
        EntityManagerFactory current = factory;
        if (current == null || !current.isOpen()) {
            synchronized (JpaUtil.class) {
                current = factory;
                if (current == null || !current.isOpen()) {
                    factory = crearFactory();
                    current = factory;
                }
            }
        }
        return current;
    }

    private static EntityManagerFactory crearFactory() {
        String password = System.getenv("DB_PASSWORD");
        if (password == null || password.isBlank()) {
            throw new IllegalStateException(
                "Falta DB_PASSWORD. Configura las variables de entorno indicadas en README.md.");
        }

        String ddlMode = variable("HIBERNATE_DDL_AUTO", "validate")
            .toLowerCase(Locale.ROOT);
        if (!DDL_MODES.contains(ddlMode)) {
            throw new IllegalArgumentException(
                "HIBERNATE_DDL_AUTO debe ser validate, none o update.");
        }

        Map<String, Object> props = new HashMap<>();
        props.put("jakarta.persistence.jdbc.driver", "org.postgresql.Driver");
        props.put("jakarta.persistence.jdbc.url",
            variable("DB_URL", "jdbc:postgresql://localhost:5432/practicas_profesionales"));
        props.put("jakarta.persistence.jdbc.user", variable("DB_USER", "postgres"));
        props.put("jakarta.persistence.jdbc.password", password);
        props.put("hibernate.hbm2ddl.auto", ddlMode);
        props.put("hibernate.show_sql", false);
        props.put("hibernate.format_sql", true);
        props.put("hibernate.jdbc.time_zone", "UTC");

        try {
            return Persistence.createEntityManagerFactory("practicasPU", props);
        } catch (RuntimeException ex) {
            throw new IllegalStateException(
                "No se pudo inicializar Hibernate ORM 7 con PostgreSQL. " +
                "Comprueba DB_URL, DB_USER, DB_PASSWORD y que el esquema SQL esté creado. " +
                "Detalle: " + ex.getMessage(), ex);
        }
    }

    private static String variable(String key, String defaultValue) {
        String result = System.getenv(key);
        return result == null || result.isBlank() ? defaultValue : result;
    }

    public static EntityManager open() {
        return factory().createEntityManager();
    }

    /** Cierra la fábrica y permite inicializarla de nuevo en otro ciclo de vida. */
    public static synchronized void shutdown() {
        if (factory != null) {
            if (factory.isOpen()) {
                factory.close();
            }
            factory = null;
        }
    }
}
