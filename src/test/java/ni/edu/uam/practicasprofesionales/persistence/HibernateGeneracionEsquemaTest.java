package ni.edu.uam.practicasprofesionales.persistence;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Demostración real del DDL generado por Hibernate 7.4 sobre una base PostgreSQL
 * independiente, creada VACÍA por el alumno. Nunca utiliza la base principal.
 *
 * Requiere HIBERNATE_SCHEMA_DEMO=true, SCHEMA_DEMO_DB_URL y DB_PASSWORD.
 * hbm2ddl.auto=create-only crea sin ejecutar DROP; el test verifica tablas,
 * claves foráneas y un segundo arranque con validate.
 */
class HibernateGeneracionEsquemaTest {
    private static final String SUFIJO_DB = "practicas_profesionales_hibernate_demo";
    private static final Set<String> TABLAS = Set.of(
        "carreras", "estudiantes", "tutores_academicos", "empresas",
        "supervisores_empresa", "convenios", "ofertas_practica",
        "postulaciones", "practicas", "bitacoras", "evaluaciones",
        "documentos", "carrera_tutor");

    @Test
    void hibernateGeneraLasTreceTablasYValidaElEsquema() throws Exception {
        Assumptions.assumeTrue(
            "true".equalsIgnoreCase(System.getenv("HIBERNATE_SCHEMA_DEMO")),
            "La demostración DDL solo se ejecuta con HIBERNATE_SCHEMA_DEMO=true.");

        String url = System.getenv("SCHEMA_DEMO_DB_URL");
        String usuario = System.getenv().getOrDefault("DB_USER", "postgres");
        String password = System.getenv("DB_PASSWORD");

        assertNotNull(url, "Es obligatorio SCHEMA_DEMO_DB_URL.");
        assertTrue(url.matches(
            "^jdbc:postgresql://(?:localhost|127\\.0\\.0\\.1):[0-9]+/"
                + SUFIJO_DB + "$"),
            "Seguridad: solo se permite PostgreSQL local en la base "
                + SUFIJO_DB + "; nunca la base principal.");
        assertNotNull(password, "Falta DB_PASSWORD.");

        // Nunca crear tablas en una base que contenga información existente.
        try (Connection conexion = DriverManager.getConnection(url, usuario, password)) {
            assertTrue(tablasPublicas(conexion).isEmpty(),
                "La base demo debe estar vacía. Utiliza una base nueva y separada.");
        }

        Configuration config = new Configuration().configure("hibernate.cfg.xml");
        config.setProperty("hibernate.connection.driver_class", "org.postgresql.Driver");
        config.setProperty("hibernate.connection.url", url);
        config.setProperty("hibernate.connection.username", usuario);
        config.setProperty("hibernate.connection.password", password);
        config.setProperty("hibernate.hbm2ddl.auto", "create-only");
        config.setProperty("hibernate.hbm2ddl.halt_on_error", "true");
        config.setProperty("hibernate.show_sql", "true");
        config.setProperty("hibernate.format_sql", "true");

        try (SessionFactory factory = config.buildSessionFactory();
             var session = factory.openSession()) {
            session.doWork(conexion -> {
                Set<String> obtenidas = tablasPublicas(conexion);
                assertTrue(obtenidas.containsAll(TABLAS),
                    "Faltan tablas generadas automáticamente: " + diferencia(obtenidas));
                assertTrue(contarClavesForaneas(conexion, "carrera_tutor") >= 2,
                    "La relación muchos-a-muchos necesita dos claves foráneas");
                System.out.println("HIBERNATE DDL: 13 tablas verificadas, "
                    + "incluida carrera_tutor con dos FK.");
            });
        }

        // Verificar lo creado con otra SessionFactory, sin volver a generar DDL.
        config.setProperty("hibernate.hbm2ddl.auto", "validate");
        config.setProperty("hibernate.show_sql", "false");
        try (SessionFactory validacion = config.buildSessionFactory()) {
            assertNotNull(validacion);
        }
        System.out.println("HIBERNATE DDL: validación de esquema correcta.");
    }

    private static Set<String> diferencia(Set<String> obtenidas) {
        Set<String> faltantes = new HashSet<>(TABLAS);
        faltantes.removeAll(obtenidas);
        return faltantes;
    }

    private static Set<String> tablasPublicas(Connection conexion) throws SQLException {
        Set<String> nombres = new HashSet<>();
        try (ResultSet rs = conexion.getMetaData()
                .getTables(null, "public", "%", new String[]{"TABLE"})) {
            while (rs.next()) {
                nombres.add(rs.getString("TABLE_NAME").toLowerCase(Locale.ROOT));
            }
        }
        return nombres;
    }

    private static int contarClavesForaneas(Connection conexion, String tabla)
            throws SQLException {
        int total = 0;
        try (ResultSet rs = conexion.getMetaData().getImportedKeys(null, "public", tabla)) {
            while (rs.next()) total++;
        }
        return total;
    }
}
