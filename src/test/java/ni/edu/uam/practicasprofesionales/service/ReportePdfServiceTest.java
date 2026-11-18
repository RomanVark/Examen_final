package ni.edu.uam.practicasprofesionales.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Prueba las cuatro plantillas JasperReports con datos de ejemplo.
 * No necesita PostgreSQL ni modifica datos reales.
 */
class ReportePdfServiceTest {
    @TempDir Path temporal;

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
        Path destino = temporal.resolve(plantilla.replace(".jrxml", ".pdf"));
        ReportePdfService.generarReporte(plantilla, List.of(datos), destino.toString());

        assertTrue(Files.isRegularFile(destino), "No se generó " + destino);
        assertTrue(Files.size(destino) > 100, "El PDF está vacío: " + destino);
        try (var archivo = Files.newInputStream(destino)) {
            byte[] cabecera = archivo.readNBytes(5);
            assertEquals("%PDF-", new String(cabecera, StandardCharsets.US_ASCII));
        }
    }
}
