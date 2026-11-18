package ni.edu.uam.practicasprofesionales.service;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Compila cada JRXML, genera su PDF y comprueba cabecera y cierre del archivo.
 * Los cuatro resultados de ejemplo quedan en target/reportes-verificados/.
 * No utiliza PostgreSQL ni presenta los datos de ejemplo como evidencias reales.
 */
class ReportePdfServiceTest {
    private static final Path CARPETA = Path.of("target", "reportes-verificados");

    @Test void generaPdfDePracticas() throws Exception {
        verificar("reporte_practicas.jrxml", Map.of(
            "id", "1", "estudiante", "Estudiante Prueba",
            "empresa", "Empresa Prueba", "estado", "EN_CURSO"));
    }

    @Test void generaPdfDeHoras() throws Exception {
        verificar("reporte_horas.jrxml", Map.of(
            "practica", "1", "estudiante", "Estudiante Prueba", "horas", "120"));
    }

    @Test void generaPdfDePostulaciones() throws Exception {
        verificar("reporte_postulaciones.jrxml", Map.of(
            "codigo", "OF-001", "oferta", "Práctica de prueba", "postulaciones", "3"));
    }

    @Test void generaPdfDePromedios() throws Exception {
        verificar("reporte_promedios.jrxml", Map.of(
            "estudiante", "Estudiante Prueba", "promedio", "90.5"));
    }

    private void verificar(String plantilla, Map<String, ?> datos) throws Exception {
        Files.createDirectories(CARPETA);
        Path destino = CARPETA.resolve(plantilla.replace(".jrxml", ".pdf"));

        ReportePdfService.generarReporte(plantilla, List.of(datos), destino.toString());

        assertTrue(Files.isRegularFile(destino), "No se generó " + destino);
        byte[] contenido = Files.readAllBytes(destino);
        assertTrue(contenido.length > 1_000, "El PDF está vacío o incompleto: " + destino);
        String inicio = new String(contenido, 0, 5, StandardCharsets.US_ASCII);
        assertEquals("%PDF-", inicio, "La cabecera no es PDF");
        String finalArchivo = new String(
            contenido, Math.max(0, contenido.length - 120),
            Math.min(120, contenido.length), StandardCharsets.ISO_8859_1);
        assertTrue(finalArchivo.contains("%%EOF"), "Falta cierre del archivo PDF: " + destino);
    }
}
