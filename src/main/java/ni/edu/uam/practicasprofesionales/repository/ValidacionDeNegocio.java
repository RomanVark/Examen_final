package ni.edu.uam.practicasprofesionales.repository;

import ni.edu.uam.practicasprofesionales.model.academico.*;
import ni.edu.uam.practicasprofesionales.model.alianzas.*;
import ni.edu.uam.practicasprofesionales.model.asignaciones.*;
import ni.edu.uam.practicasprofesionales.model.seguimiento.*;
import ni.edu.uam.practicasprofesionales.model.enums.EstadoConvenio;
import jakarta.persistence.EntityManager;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Locale;
import java.util.regex.Pattern;

/** Validaciones adicionales a CHECK/UNIQUE/FOREIGN KEY de PostgreSQL. */
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
                if (campo.isAnnotationPresent(jakarta.persistence.JoinColumn.class) && valor == null && !campo.getAnnotation(jakarta.persistence.JoinColumn.class).nullable())
                    throw new IllegalArgumentException("Seleccione una opción para " + campo.getName() + ".");
                if (campo.getName().equals("correo") && valor instanceof String correo && !EMAIL.matcher(correo).matches())
                    throw new IllegalArgumentException("El correo tiene un formato incorrecto.");
            } catch (IllegalAccessException ex) { throw new IllegalStateException(ex); }
        }
        if (entity instanceof Empresa x && x.getRuc() != null && !RUC.matcher(x.getRuc().strip()).matches())
            throw new IllegalArgumentException("El RUC solo puede contener letras, números y guiones (mínimo 4 caracteres).");
        if (entity instanceof SupervisorEmpresa x) validarEmpresaAsociada(x.getEmpresa(), "el supervisor");
        if (entity instanceof Convenio x) {
            validarEmpresaAsociada(x.getEmpresa(), "el convenio");
            fechas(x.getFechaInicio(), x.getFechaFin());
            validarVigenciaConvenio(x);
        }
        if (entity instanceof OfertaPractica x) {
            fechas(x.getFechaInicio(), x.getFechaFin());
            positivo(x.getCupos(), "Los cupos");
        }
        if (entity instanceof Practica x) {
            fechas(x.getFechaInicio(), x.getFechaFin());
            positivo(x.getHorasRequeridas(), "Las horas requeridas");
            if (x.getOferta() != null && x.getSupervisorEmpresa() != null && x.getOferta().getEmpresa() != null
                    && x.getSupervisorEmpresa().getEmpresa() != null
                    && !x.getOferta().getEmpresa().getId().equals(x.getSupervisorEmpresa().getEmpresa().getId()))
                throw new IllegalArgumentException("El supervisor debe pertenecer a la empresa de la oferta.");
        }
        if (entity instanceof Bitacora x) {
            positivo(x.getHoras(), "Las horas");
            if (x.getHoras() != null && x.getHoras() > 24) throw new IllegalArgumentException("Una bitácora no puede superar 24 horas por día.");
        }
        if (entity instanceof Evaluacion x && x.getCalificacion() != null &&
                (x.getCalificacion().compareTo(BigDecimal.ZERO) < 0 || x.getCalificacion().compareTo(new BigDecimal("100")) > 0))
            throw new IllegalArgumentException("La calificación debe estar entre 0 y 100.");
    }
    /** Normaliza RUC para comparar sin diferencias de mayúsculas o espacios externos. */
    public static String normalizarRuc(String ruc) {
        return ruc == null ? null : ruc.strip().toUpperCase(Locale.ROOT);
    }

    /** Comprueba duplicados dentro de la misma transacción y permite conservar el RUC al editar. */
    public static void validarUnicidad(EntityManager em, Object entity) {
        if (!(entity instanceof Empresa empresa) || empresa.getRuc() == null) return;
        String ruc = normalizarRuc(empresa.getRuc());
        if (ruc.isBlank()) return;

        Long duplicados;
        if (empresa.getId() == null) {
            duplicados = em.createQuery(
                    "select count(e) from Empresa e where upper(trim(e.ruc)) = :ruc", Long.class)
                .setParameter("ruc", ruc)
                .getSingleResult();
        } else {
            duplicados = em.createQuery(
                    "select count(e) from Empresa e where upper(trim(e.ruc)) = :ruc and e.id <> :id", Long.class)
                .setParameter("ruc", ruc)
                .setParameter("id", empresa.getId())
                .getSingleResult();
        }
        if (duplicados > 0) throw new IllegalArgumentException("Ya existe una empresa registrada con ese RUC.");
    }

    private static void validarEmpresaAsociada(Empresa empresa, String descripcion) {
        if (empresa == null || empresa.getId() == null)
            throw new IllegalArgumentException("Seleccione una empresa registrada para " + descripcion + ".");
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
        if (valor != null && valor <= 0) throw new IllegalArgumentException(descripcion + " deben ser mayores a cero.");
    }
}
