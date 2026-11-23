package ni.edu.uam.practicasprofesionales.service;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperPrintManager;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;
import net.sf.jasperreports.engine.util.JRSaver;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Compila plantillas JRXML institucionales, prepara JasperPrint y reutiliza
 * exactamente el mismo documento para el PDF y la vista previa JavaFX.
 */
public final class ReportePdfService {
    private ReportePdfService() { }

    public static JasperPrint prepararReporte(
            String nombrePlantilla, Collection<Map<String, ?>> datos) throws Exception {
        if (nombrePlantilla == null || !nombrePlantilla.matches("reporte_[a-z]+\\.jrxml")) {
            throw new IllegalArgumentException("Nombre de plantilla no permitido.");
        }
        if (datos == null) throw new IllegalArgumentException("Los datos son obligatorios.");
        String ruta = "/reportes/" + nombrePlantilla;
        try (InputStream recurso = ReportePdfService.class.getResourceAsStream(ruta)) {
            if (recurso == null) {
                throw new IllegalArgumentException("No se encontró la plantilla: " + ruta);
            }
            var plantilla = JasperCompileManager.compileReport(recurso);
            Path compilados = Path.of("target", "reportes-compilados");
            Files.createDirectories(compilados);
            JRSaver.saveObject(plantilla,
                compilados.resolve(nombrePlantilla.replace(".jrxml", ".jasper")).toFile());

            Map<String, Object> parametros = new HashMap<>();
            parametros.put("TOTAL_REGISTROS", datos.size());
            Indicador indicador = calcularIndicador(nombrePlantilla, datos);
            parametros.put("INDICADOR_TITULO", indicador.titulo());
            parametros.put("INDICADOR_VALOR", indicador.valor());
            return JasperFillManager.fillReport(
                plantilla, parametros, new JRMapCollectionDataSource(datos));
        }
    }

    public static void generarReporte(
            String nombrePlantilla, Collection<Map<String, ?>> datos,
            String rutaArchivo) throws Exception {
        exportarPdf(prepararReporte(nombrePlantilla, datos), Path.of(rutaArchivo));
    }

    public static void exportarPdf(JasperPrint reporte, Path destino) throws Exception {
        if (reporte == null || destino == null) {
            throw new IllegalArgumentException("Reporte y destino son obligatorios.");
        }
        JasperExportManager.exportReportToPdfFile(reporte, destino.toString());
    }

    public static void imprimir(JasperPrint reporte) throws Exception {
        if (reporte == null) throw new IllegalArgumentException("No se ha cargado el reporte.");
        JasperPrintManager.printReport(reporte, true);
    }

    /**
     * Convierte la página real de JasperPrint a PNG para mostrarla en un ImageView.
     * La conversión AWT se ejecuta en un hilo de trabajo, nunca en el hilo JavaFX.
     */
    public static byte[] renderizarPagina(JasperPrint reporte, int pagina, float zoom)
            throws Exception {
        if (reporte == null || pagina < 0 || pagina >= reporte.getPages().size()
            || zoom < 0.5f || zoom > 2.0f) {
            throw new IllegalArgumentException("Página o zoom fuera de rango.");
        }
        java.awt.Image render = JasperPrintManager.printPageToImage(reporte, pagina, zoom);
        int ancho = Math.max(1, render.getWidth(null));
        int alto = Math.max(1, render.getHeight(null));
        BufferedImage imagen = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_RGB);
        Graphics2D graficos = imagen.createGraphics();
        try {
            graficos.setColor(java.awt.Color.WHITE);
            graficos.fillRect(0, 0, ancho, alto);
            graficos.drawImage(render, 0, 0, null);
        } finally {
            graficos.dispose();
        }
        try (ByteArrayOutputStream salida = new ByteArrayOutputStream()) {
            if (!ImageIO.write(imagen, "png", salida)) {
                throw new IOException("No se pudo codificar la página en PNG.");
            }
            return salida.toByteArray();
        }
    }

    private record Indicador(String titulo, String valor) { }

    private static Indicador calcularIndicador(
            String nombre, Collection<Map<String, ?>> datos) {
        if (nombre.equals("reporte_practicas.jrxml")) {
            long activas = datos.stream()
                .filter(d -> "EN_CURSO".equalsIgnoreCase(texto(d, "estado"))).count();
            return new Indicador("PRÁCTICAS EN CURSO", Long.toString(activas));
        }
        String campo;
        String titulo;
        boolean promedio;
        switch (nombre) {
            case "reporte_horas.jrxml", "reporte_seguimiento_horas.jrxml" -> {
                campo = "horas"; titulo = "HORAS REGISTRADAS"; promedio = false;
            }
            case "reporte_postulaciones.jrxml" -> {
                campo = "postulaciones"; titulo = "POSTULACIONES TOTALES"; promedio = false;
            }
            case "reporte_promedios.jrxml", "reporte_seguimiento_promedios.jrxml" -> {
                campo = "promedio"; titulo = "PROMEDIO GENERAL"; promedio = true;
            }
            default -> { return new Indicador("REGISTROS", String.valueOf(datos.size())); }
        }
        BigDecimal suma = BigDecimal.ZERO;
        int valoresValidos = 0;
        for (Map<String, ?> fila : datos) {
            String texto = texto(fila, campo);
            if (!texto.isBlank()) {
                try {
                    suma = suma.add(new BigDecimal(texto.trim().replace(",", ".")));
                    valoresValidos++;
                } catch (NumberFormatException ex) {
                    // El registro conserva su valor textual; no suma a los indicadores.
                }
            }
        }
        if (promedio && valoresValidos > 0) {
            suma = suma.divide(BigDecimal.valueOf(valoresValidos), 2, RoundingMode.HALF_UP);
        }
        if (promedio && valoresValidos == 0) {
            return new Indicador(titulo, "Sin datos");
        }
        return new Indicador(titulo, suma.stripTrailingZeros().toPlainString());
    }

    private static String texto(Map<String, ?> mapa, String nombre) {
        Object valor = mapa.get(nombre);
        return valor == null ? "" : valor.toString();
    }
}
