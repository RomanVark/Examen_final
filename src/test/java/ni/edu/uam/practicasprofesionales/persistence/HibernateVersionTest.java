package ni.edu.uam.practicasprofesionales.persistence;

import org.hibernate.Version;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class HibernateVersionTest {
    @Test
    void utilizaHibernate74GestionadoPorSpringBoot412() {
        String version = Version.getVersionString();
        assertTrue(version.startsWith("7.4."),
            () -> "Se esperaba Hibernate ORM 7.4.x, pero se encontró: " + version);
    }
}
