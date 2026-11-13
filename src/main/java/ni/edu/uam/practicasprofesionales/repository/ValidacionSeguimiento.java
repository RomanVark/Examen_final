package ni.edu.uam.practicasprofesionales.repository;

import ni.edu.uam.practicasprofesionales.model.seguimiento.Bitacora;
import ni.edu.uam.practicasprofesionales.model.seguimiento.Documento;
import ni.edu.uam.practicasprofesionales.model.seguimiento.Evaluacion;
import java.math.BigDecimal;

/** Reglas de negocio exclusivas del módulo de seguimiento. */
public final class ValidacionSeguimiento {
    private static final BigDecimal NOTA_MINIMA = BigDecimal.ZERO;
    private static final BigDecimal NOTA_MAXIMA = new BigDecimal("100.00");

    private ValidacionSeguimiento() { }

    public static void validar(Bitacora bitacora) {
        if (bitacora == null) throw new IllegalArgumentException("La bitácora es obligatoria.");
        requerido(bitacora.getPractica() != null, "Seleccione la práctica.");
        requerido(bitacora.getFecha() != null, "Indique la fecha de la bitácora.");
        texto(bitacora.getActividad(), "Describa la actividad.");
        Integer horas = bitacora.getHoras();
        if (horas == null || horas < 1 || horas > 24)
            throw new IllegalArgumentException("Las horas deben estar entre 1 y 24 por día.");
        requerido(bitacora.getValidada() != null, "Indique si la bitácora está validada.");
    }

    public static void validar(Evaluacion evaluacion) {
        if (evaluacion == null) throw new IllegalArgumentException("La evaluación es obligatoria.");
        requerido(evaluacion.getPractica() != null, "Seleccione la práctica.");
        requerido(evaluacion.getFecha() != null, "Indique la fecha de evaluación.");
        texto(evaluacion.getEvaluador(), "Indique el nombre del evaluador.");
        BigDecimal nota = evaluacion.getCalificacion();
        if (nota == null || nota.compareTo(NOTA_MINIMA) < 0 || nota.compareTo(NOTA_MAXIMA) > 0)
            throw new IllegalArgumentException("La calificación debe estar entre 0 y 100.");
    }

    public static void validar(Documento documento) {
        if (documento == null) throw new IllegalArgumentException("El documento es obligatorio.");
        requerido(documento.getPractica() != null, "Seleccione la práctica.");
        texto(documento.getTipo(), "Seleccione el tipo de documento.");
        texto(documento.getNombreArchivo(), "Indique el nombre del archivo.");
        texto(documento.getRutaArchivo(), "Indique la ruta del archivo.");
        requerido(documento.getFechaEntrega() != null, "Indique la fecha de entrega.");
        requerido(documento.getVerificado() != null, "Indique si el documento fue verificado.");
    }

    private static void texto(String valor, String mensaje) {
        if (valor == null || valor.trim().isEmpty()) throw new IllegalArgumentException(mensaje);
    }

    private static void requerido(boolean condicion, String mensaje) {
        if (!condicion) throw new IllegalArgumentException(mensaje);
    }
}
