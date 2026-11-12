package ni.edu.uam.practicasprofesionales.persistence;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.HashMap;
import java.util.Map;

/** Configuración central de Hibernate; credenciales exclusivamente desde variables de entorno. */
public final class JpaUtil {
    private static EntityManagerFactory factory;
    private JpaUtil() { }

    public static synchronized EntityManagerFactory factory() {
        if (factory == null) {
            String password = System.getenv("DB_PASSWORD");
            if (password == null || password.isBlank()) {
                throw new IllegalStateException("Falta DB_PASSWORD. Configura las variables de entorno como indica README.md.");
            }
            Map<String, Object> props = new HashMap<>();
            props.put("jakarta.persistence.jdbc.driver", "org.postgresql.Driver");
            props.put("jakarta.persistence.jdbc.url", variable("DB_URL", "jdbc:postgresql://localhost:5432/practicas_profesionales"));
            props.put("jakarta.persistence.jdbc.user", variable("DB_USER", "postgres"));
            props.put("jakarta.persistence.jdbc.password", password);
            factory = Persistence.createEntityManagerFactory("practicasPU", props);
        }
        return factory;
    }

    private static String variable(String key, String defaultValue) {
        String result = System.getenv(key);
        return result == null || result.isBlank() ? defaultValue : result;
    }

    public static EntityManager open() { return factory().createEntityManager(); }

    public static synchronized void shutdown() {
        if (factory != null && factory.isOpen()) factory.close();
    }
}
