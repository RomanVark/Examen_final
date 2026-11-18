package ni.edu.uam.practicasprofesionales.repository;

import ni.edu.uam.practicasprofesionales.model.academico.Carrera;
import ni.edu.uam.practicasprofesionales.model.academico.Estudiante;
import ni.edu.uam.practicasprofesionales.model.academico.TutorAcademico;

import java.util.List;
import java.util.Locale;

/** Consultas HQL de dominio: parámetros, JOIN FETCH y agrupaciones. */
public final class AcademicoDao extends GenericDao<Estudiante> {
    public AcademicoDao() {
        super(Estudiante.class);
    }

    public List<Estudiante> buscarEstudiantes(String termino) {
        String valor = "%" + normalizar(termino) + "%";
        return consulta(s -> s.createQuery(
                "select e from Estudiante e join fetch e.carrera c " +
                "where lower(e.carnet) like :busca or lower(e.nombres) like :busca " +
                "or lower(e.apellidos) like :busca or lower(c.nombre) like :busca " +
                "order by e.apellidos, e.nombres", Estudiante.class)
            .setParameter("busca", valor)
            .getResultList());
    }

    public List<TutorAcademico> buscarTutores(String termino) {
        String valor = "%" + normalizar(termino) + "%";
        return consulta(s -> s.createQuery(
                "select t from TutorAcademico t where lower(t.nombres) like :q " +
                "or lower(t.apellidos) like :q or lower(t.correo) like :q " +
                "order by t.apellidos", TutorAcademico.class)
            .setParameter("q", valor)
            .getResultList());
    }

    public List<Object[]> estudiantesPorCarrera() {
        return consulta(s -> s.createQuery(
            "select c.nombre, count(e.id) from Carrera c left join c.estudiantes e " +
            "group by c.id, c.nombre order by c.nombre", Object[].class).getResultList());
    }

    /** Inicializa una relación LAZY antes de cerrar la sesión. */
    public List<Carrera> listarCarrerasConEstudiantes() {
        return consulta(s -> s.createQuery(
            "select distinct c from Carrera c left join fetch c.estudiantes " +
            "order by c.nombre", Carrera.class).getResultList());
    }

    private static String normalizar(String valor) {
        return valor == null ? "" : valor.strip().toLowerCase(Locale.ROOT);
    }
}
