package ni.edu.uam.practicasprofesionales.service;

import ni.edu.uam.practicasprofesionales.persistence.JpaUtil;
import jakarta.persistence.EntityManager;
import java.util.List;

/** Consultas HQL con JOIN sobre entidades relacionadas (no SQL/JDBC directo). */
public final class ReporteHqlService {
    public record Reporte(String titulo, List<String> columnas, String hql) {
        @Override public String toString() { return titulo; }
    }
    public static final List<Reporte> REPORTES = List.of(
        new Reporte("Prácticas por estudiante y empresa", List.of("ID", "Estudiante", "Empresa", "Estado"),
            "select p.id, concat(e.nombres, ' ', e.apellidos), emp.razonSocial, p.estado " +
            "from Practica p join p.estudiante e join p.oferta o join o.empresa emp order by p.id"),
        new Reporte("Horas registradas por práctica", List.of("Práctica", "Estudiante", "Horas registradas"),
            "select p.id, concat(e.nombres, ' ', e.apellidos), coalesce(sum(b.horas), 0) " +
            "from Practica p join p.estudiante e left join p.bitacoras b " +
            "group by p.id, e.nombres, e.apellidos order by p.id"),
        new Reporte("Postulaciones por oferta", List.of("Código", "Oferta", "Postulaciones"),
            "select o.codigo, o.titulo, count(pos.id) " +
            "from OfertaPractica o left join o.postulaciones pos " +
            "group by o.id, o.codigo, o.titulo order by o.codigo"),
        new Reporte("Promedio de evaluación por estudiante", List.of("Estudiante", "Promedio"),
            "select concat(e.nombres, ' ', e.apellidos), avg(ev.calificacion) " +
            "from Evaluacion ev join ev.practica p join p.estudiante e " +
            "group by e.id, e.nombres, e.apellidos order by e.apellidos")
    );
    private ReporteHqlService() { }

    public static List<Object[]> ejecutar(Reporte reporte) {
        try (EntityManager em = JpaUtil.open()) {
            return em.createQuery(reporte.hql(), Object[].class).getResultList();
        }
    }
}
