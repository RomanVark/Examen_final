package ni.edu.uam.practicasprofesionales.service;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;

import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ReportePdfService {
    private ReportePdfService() {
    }

    public static void generarReporte(
            String nombrePlantilla,
            Collection<Map<String, ?>> datos,
            String rutaArchivo) throws Exception {

        String ruta = "/reportes/" + nombrePlantilla;

        try (InputStream archivo =
                     ReportePdfService.class.getResourceAsStream(ruta)) {

            if (archivo == null) {
                throw new IllegalArgumentException(
                        "No se encontró la plantilla: " + ruta);
            }

            var reporteCompilado =
                    JasperCompileManager.compileReport(archivo);

            JRMapCollectionDataSource fuente =
                    new JRMapCollectionDataSource(datos);

            Map<String, Object> parametros = new HashMap<>();

            JasperPrint reporteLleno =
                    JasperFillManager.fillReport(
                            reporteCompilado,
                            parametros,
                            fuente);

            JasperExportManager.exportReportToPdfFile(
                    reporteLleno,
                    rutaArchivo);
        }
    }
}
