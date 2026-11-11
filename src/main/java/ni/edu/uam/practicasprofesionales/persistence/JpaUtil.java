package ni.edu.uam.practicasprofesionales.persistence;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.HashMap;
import java.util.Map;

public final class JpaUtil {
 private static EntityManagerFactory factory;
 private JpaUtil() {}
 public static synchronized EntityManager abrir() {
  if (factory == null) {
   Map<String, Object> props = new HashMap<>();
   props.put("jakarta.persistence.jdbc.url", valor("DB_URL", "jdbc:postgresql://localhost:5432/practicas_profesionales"));
   props.put("jakarta.persistence.jdbc.user", valor("DB_USER", "postgres"));
   String clave = System.getenv("DB_PASSWORD");
   if (clave == null || clave.isBlank()) {
    throw new IllegalStateException("Defina DB_PASSWORD como variable de entorno; no se guardan contraseñas en el repositorio.");
   }
   props.put("jakarta.persistence.jdbc.password", clave);
   factory = Persistence.createEntityManagerFactory("practicasPU", props);
  }
  return factory.createEntityManager();
 }
 private static String valor(String key, String defecto) {
  String dato = System.getenv(key);
  return dato == null || dato.isBlank() ? defecto : dato;
 }
 public static synchronized void close() {
  if (factory != null && factory.isOpen()) factory.close();
  factory = null;
 }
}
