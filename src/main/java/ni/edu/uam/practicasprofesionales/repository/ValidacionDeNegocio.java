package ni.edu.uam.practicasprofesionales.repository;

import ni.edu.uam.practicasprofesionales.model.academico.*;
import ni.edu.uam.practicasprofesionales.model.alianzas.*;
import ni.edu.uam.practicasprofesionales.model.asignaciones.*;
import ni.edu.uam.practicasprofesionales.model.seguimiento.*;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.regex.Pattern;

/** Validaciones adicionales a CHECK/UNIQUE/FOREIGN KEY de PostgreSQL. */
public final class ValidacionDeNegocio {
    private static final Pattern EMAIL = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
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
        if (entity instanceof Convenio x) fechas(x.getFechaInicio(), x.getFechaFin());
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
    private static void fechas(LocalDate inicio, LocalDate fin) {
        if (inicio != null && fin != null && fin.isBefore(inicio))
            throw new IllegalArgumentException("La fecha final no puede ser anterior a la fecha inicial.");
    }
    private static void positivo(Integer valor, String descripcion) {
        if (valor != null && valor <= 0) throw new IllegalArgumentException(descripcion + " deben ser mayores a cero.");
    }
}
