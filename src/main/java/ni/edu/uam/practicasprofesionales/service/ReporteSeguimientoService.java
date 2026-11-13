package ni.edu.uam.practicasprofesionales.service;

import jakarta.persistence.EntityManager;
import java.util.Arrays;
import java.util.List;

/** Consultas HQL de horas registradas y evaluaciones. */
public final class ReporteSeguimientoService {
    public static final class Reporte {
        private final String titulo;
        private final List<String> columnas;
        private final String hql;

        public Reporte(String titulo, List<String> columnas, String hql) {
            this.titulo = titulo;
            this.columnas = columnas;
            this.hql = hql;
        }
        public String titulo() { return titulo; }
        public List<String> columnas() { return columnas; }
        public String hql() { return hql; }
        @Override public String toString() { return titulo; }
    }

    public static final Reporte HORAS_POR_PRACTICA = new Reporte(
        "Horas registradas por práctica",
        Arrays.asList("Práctica", "Estudiante", "Horas registradas"),
        "select p.id, concat(e.nombres, ' ', e.apellidos), coalesce(sum(b.horas), 0) " +
        "from Practica p join p.estudiante e left join p.bitacoras b " +
        "group by p.id, e.nombres, e.apellidos order by p.id"
    );

    public static final Reporte PROMEDIO_EVALUACIONES = new Reporte(
        "Promedio de evaluación por estudiante",
        Arrays.asList("Estudiante", "Promedio"),
        "select concat(e.nombres, ' ', e.apellidos), avg(ev.calificacion) " +
        "from Evaluacion ev join ev.practica p join p.estudiante e " +
        "group by e.id, e.nombres, e.apellidos order by e.apellidos"
    );

    public static final List<Reporte> REPORTES =
        Arrays.asList(HORAS_POR_PRACTICA, PROMEDIO_EVALUACIONES);

    private ReporteSeguimientoService() { }

    public static List<Object[]> ejecutar(EntityManager entityManager, Reporte reporte) {
        if (entityManager == null)
            throw new IllegalArgumentException("La conexión de persistencia no está disponible.");
        if (reporte == null) throw new IllegalArgumentException("Seleccione un reporte.");
        return entityManager.createQuery(reporte.hql(), Object[].class).getResultList();
    }
}
