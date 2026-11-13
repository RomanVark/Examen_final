package ni.edu.uam.practicasprofesionales.repository;

import ni.edu.uam.practicasprofesionales.model.academico.*;
import ni.edu.uam.practicasprofesionales.model.alianzas.*;
import ni.edu.uam.practicasprofesionales.model.asignaciones.*;
import ni.edu.uam.practicasprofesionales.model.seguimiento.*;
import ni.edu.uam.practicasprofesionales.model.enums.EstadoConvenio;
import ni.edu.uam.practicasprofesionales.model.enums.EstadoOferta;
import jakarta.persistence.EntityManager;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Locale;
import java.util.regex.Pattern;

/** Validaciones adicionales a CHECK, UNIQUE y FOREIGN KEY de PostgreSQL. */
public final class ValidacionDeNegocio {
    private static final Pattern EMAIL = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
    private static final Pattern RUC = Pattern.compile("^[A-Za-z0-9][A-Za-z0-9-]{3,149}$");
    private ValidacionDeNegocio() { }

    public static void validar(Object entity) {
        if (entity == null) throw new IllegalArgumentException("Entidad vacía.");

        for (Field campo : entity.getClass().getDeclaredFields()) {
            if (campo.getName().equals("id") || java.util.List.class.isAssignableFrom(campo.getType())) continue;
            campo.setAccessible(true);
            try {
                Object valor = campo.get(entity);
                if (campo.isAnnotationPresent(jakarta.persistence.Column.class)) {
                    var column = campo.getAnnotation(jakarta.persistence.Column.class);
                    if (!column.nullable() && (valor == null || (valor instanceof String s && s.isBlank())))
                        throw new IllegalArgumentException("El campo " + campo.getName() + " es obligatorio.");
                    if (valor instanceof String texto && texto.length() > column.length())
                        throw new IllegalArgumentException("El campo " + campo.getName() + " supera " + column.length() + " caracteres.");
                }
                if (campo.isAnnotationPresent(jakarta.persistence.JoinColumn.class)
                        && valor == null && !campo.getAnnotation(jakarta.persistence.JoinColumn.class).nullable())
                    throw new IllegalArgumentException("Seleccione una opción para " + campo.getName() + ".");
                if (campo.getName().equals("correo") && valor instanceof String correo
                        && !EMAIL.matcher(correo).matches())
                    throw new IllegalArgumentException("El correo tiene un formato incorrecto.");
            } catch (IllegalAccessException ex) {
                throw new IllegalStateException(ex);
            }
        }

        if (entity instanceof Empresa x && x.getRuc() != null && !RUC.matcher(x.getRuc().strip()).matches())
            throw new IllegalArgumentException("El RUC solo puede contener letras, números y guiones (mínimo 4 caracteres).");
        if (entity instanceof SupervisorEmpresa x) validarEmpresaAsociada(x.getEmpresa(), "el supervisor");
        if (entity instanceof Convenio x) {
            validarEmpresaAsociada(x.getEmpresa(), "el convenio");
            fechas(x.getFechaInicio(), x.getFechaFin());
            validarVigenciaConvenio(x);
        }

        // Reglas del módulo de asignaciones.
        if (entity instanceof OfertaPractica x) {
            validarEmpresaAsociada(x.getEmpresa(), "la oferta");
            fechas(x.getFechaInicio(), x.getFechaFin());
            positivo(x.getCupos(), "Los cupos");
            if (x.getEstado() == null) throw new IllegalArgumentException("Seleccione el estado de la oferta.");
        }
        if (entity instanceof Postulacion x) {
            validarReferencia(x.getEstudiante(), "un estudiante");
            if (x.getOferta() == null || x.getOferta().getId() == null)
                throw new IllegalArgumentException("Seleccione una oferta registrada.");
            if (x.getOferta().getEstado() != EstadoOferta.ABIERTA)
                throw new IllegalArgumentException("Solo se puede postular a ofertas ABIERTAS.");
            if (x.getFecha() != null && x.getFecha().isAfter(LocalDate.now()))
                throw new IllegalArgumentException("La fecha de postulación no puede estar en el futuro.");
        }
        if (entity instanceof Practica x) {
            validarReferencia(x.getEstudiante(), "un estudiante");
            validarReferencia(x.getOferta(), "una oferta");
            validarReferencia(x.getTutorAcademico(), "un tutor académico");
            validarReferencia(x.getSupervisorEmpresa(), "un supervisor de empresa");
            fechas(x.getFechaInicio(), x.getFechaFin());
            positivo(x.getHorasRequeridas(), "Las horas requeridas");
            validarSupervisorDeEmpresa(x);
            if (x.getOferta() != null) {
                LocalDate inicioOferta = x.getOferta().getFechaInicio();
                LocalDate finOferta = x.getOferta().getFechaFin();
                if (inicioOferta != null && x.getFechaInicio() != null && x.getFechaInicio().isBefore(inicioOferta))
                    throw new IllegalArgumentException("La práctica no puede iniciar antes que la oferta.");
                if (finOferta != null && x.getFechaFin() != null && x.getFechaFin().isAfter(finOferta))
                    throw new IllegalArgumentException("La práctica no puede finalizar después de la oferta.");
            }
        }

        if (entity instanceof Bitacora x) {
            positivo(x.getHoras(), "Las horas");
            if (x.getHoras() != null && x.getHoras() > 24)
                throw new IllegalArgumentException("Una bitácora no puede superar 24 horas por día.");
        }
        if (entity instanceof Evaluacion x && x.getCalificacion() != null &&
                (x.getCalificacion().compareTo(BigDecimal.ZERO) < 0
                || x.getCalificacion().compareTo(new BigDecimal("100")) > 0))
            throw new IllegalArgumentException("La calificación debe estar entre 0 y 100.");
    }

    /** Comprueba unicidad de RUC, postulaciones y prácticas por estudiante + oferta. */
    public static void validarUnicidad(EntityManager em, Object entity) {
        if (entity instanceof Empresa empresa && empresa.getRuc() != null) {
            String ruc = normalizarRuc(empresa.getRuc());
            if (!ruc.isBlank()) {
                var query = em.createQuery(
                    "select count(e) from Empresa e where upper(trim(e.ruc)) = :ruc" +
                    (empresa.getId() == null ? "" : " and e.id <> :id"), Long.class);
                query.setParameter("ruc", ruc);
                if (empresa.getId() != null) query.setParameter("id", empresa.getId());
                if (query.getSingleResult() > 0)
                    throw new IllegalArgumentException("Ya existe una empresa registrada con ese RUC.");
            }
        }
        if (entity instanceof Postulacion p) {
            long duplicados = contarDuplicados(em, "Postulacion", p.getEstudiante().getId(),
                    p.getOferta().getId(), p.getId());
            if (duplicados > 0)
                throw new IllegalArgumentException("Este estudiante ya se postuló a esa oferta.");
        }
        if (entity instanceof Practica p) {
            long duplicados = contarDuplicados(em, "Practica", p.getEstudiante().getId(),
                    p.getOferta().getId(), p.getId());
            if (duplicados > 0)
                throw new IllegalArgumentException("Ya existe una práctica para este estudiante y esta oferta.");
        }
    }

    private static long contarDuplicados(EntityManager em, String entidad, Long estudianteId,
                                         Long ofertaId, Long idActual) {
        String hql = "select count(x) from " + entidad +
                " x where x.estudiante.id = :estudiante and x.oferta.id = :oferta" +
                (idActual == null ? "" : " and x.id <> :id");
        var query = em.createQuery(hql, Long.class)
                .setParameter("estudiante", estudianteId)
                .setParameter("oferta", ofertaId);
        if (idActual != null) query.setParameter("id", idActual);
        return query.getSingleResult();
    }

    public static String normalizarRuc(String ruc) {
        return ruc == null ? null : ruc.strip().toUpperCase(Locale.ROOT);
    }

    private static void validarReferencia(Object referencia, String descripcion) {
        if (referencia == null) throw new IllegalArgumentException("Seleccione " + descripcion + ".");
        try {
            Object id = referencia.getClass().getMethod("getId").invoke(referencia);
            if (id == null) throw new IllegalArgumentException("Seleccione " + descripcion + " registrado.");
        } catch (ReflectiveOperationException ex) {
            throw new IllegalArgumentException("No se pudo comprobar la referencia a " + descripcion + ".", ex);
        }
    }

    private static void validarEmpresaAsociada(Empresa empresa, String descripcion) {
        if (empresa == null || empresa.getId() == null)
            throw new IllegalArgumentException("Seleccione una empresa registrada para " + descripcion + ".");
    }

    private static void validarSupervisorDeEmpresa(Practica practica) {
        Empresa empresaOferta = practica.getOferta().getEmpresa();
        Empresa empresaSupervisor = practica.getSupervisorEmpresa().getEmpresa();
        if (empresaOferta == null || empresaSupervisor == null ||
                empresaOferta.getId() == null || empresaSupervisor.getId() == null ||
                !empresaOferta.getId().equals(empresaSupervisor.getId()))
            throw new IllegalArgumentException("El supervisor debe pertenecer a la misma empresa de la oferta.");
        if (!Boolean.TRUE.equals(practica.getSupervisorEmpresa().getActivo()))
            throw new IllegalArgumentException("No se puede asignar un supervisor inactivo.");
    }

    private static void validarVigenciaConvenio(Convenio convenio) {
        if (convenio.getEstado() == null || convenio.getFechaInicio() == null || convenio.getFechaFin() == null) return;
        LocalDate hoy = LocalDate.now();
        if (convenio.getEstado() == EstadoConvenio.VIGENTE
                && (hoy.isBefore(convenio.getFechaInicio()) || hoy.isAfter(convenio.getFechaFin())))
            throw new IllegalArgumentException("Un convenio solo puede estar VIGENTE dentro de sus fechas de vigencia.");
        if (convenio.getEstado() == EstadoConvenio.VENCIDO && !hoy.isAfter(convenio.getFechaFin()))
            throw new IllegalArgumentException("Un convenio solo puede marcarse VENCIDO después de su fecha final.");
    }

    private static void fechas(LocalDate inicio, LocalDate fin) {
        if (inicio != null && fin != null && fin.isBefore(inicio))
            throw new IllegalArgumentException("La fecha final no puede ser anterior a la fecha inicial.");
    }

    private static void positivo(Integer valor, String descripcion) {
        if (valor != null && valor <= 0)
            throw new IllegalArgumentException(descripcion + " deben ser mayores a cero.");
    }
}
