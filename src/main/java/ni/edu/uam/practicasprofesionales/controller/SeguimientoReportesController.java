package ni.edu.uam.practicasprofesionales.controller;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;
import ni.edu.uam.practicasprofesionales.service.ExportadorCsv;
import ni.edu.uam.practicasprofesionales.service.ReporteExcelService;
import ni.edu.uam.practicasprofesionales.service.ReporteSeguimientoService;
import ni.edu.uam.practicasprofesionales.util.FxDb;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/** Controlador de los dos reportes asignados al módulo de seguimiento. */
public class SeguimientoReportesController {
    @FXML private ComboBox<ReporteSeguimientoService.Reporte> selectorReporte;
    @FXML private TableView<List<String>> tablaReporte;
    @FXML private Label textoResultado;
    private List<List<String>> filasActuales = new ArrayList<>();

    @FXML public void initialize() {
        selectorReporte.setItems(FXCollections.observableArrayList(ReporteSeguimientoService.REPORTES));
        selectorReporte.getSelectionModel().selectFirst();
        textoResultado.setText("Seleccione un reporte cuando la conexión esté disponible.");
    }

    @FXML public void cargarReporte() {
        ReporteSeguimientoService.Reporte reporte = selectorReporte.getValue();
        if (reporte == null) return;
        textoResultado.setText("Consultando PostgreSQL...");
        FxDb.ejecutar(() -> ReporteSeguimientoService.ejecutar(reporte), resultado -> {
            if (selectorReporte.getValue() != reporte) return;
            filasActuales = new ArrayList<>();
            for (Object[] fila : resultado) {
                List<String> valores = new ArrayList<>();
                for (Object valor : fila) valores.add(valor == null ? "" : valor.toString());
                filasActuales.add(valores);
            }
            tablaReporte.getColumns().clear();
            for (int i = 0; i < reporte.columnas().size(); i++) {
                final int indice = i;
                TableColumn<List<String>, String> columna = new TableColumn<>(reporte.columnas().get(i));
                columna.setCellValueFactory(dato ->
                    new ReadOnlyStringWrapper(dato.getValue().get(indice)));
                columna.setPrefWidth(230);
                tablaReporte.getColumns().add(columna);
            }
            tablaReporte.setItems(FXCollections.observableArrayList(filasActuales));
            textoResultado.setText(filasActuales.size() + " resultado(s)");
        }, error -> textoResultado.setText(
            "No se pudo ejecutar el reporte: " + error.getMessage()));
    }

    @FXML public void exportarCsv() {
        ReporteSeguimientoService.Reporte reporte = selectorReporte.getValue();
        if (reporte == null || filasActuales.isEmpty()) {
            textoResultado.setText("Ejecute un reporte con resultados antes de exportar.");
            return;
        }
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Exportar reporte de seguimiento");
        chooser.setInitialFileName("reporte_seguimiento.csv");
        chooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Archivo CSV", "*.csv"));
        File archivo = chooser.showSaveDialog(tablaReporte.getScene().getWindow());
        if (archivo == null) return;
        try {
            ExportadorCsv.exportar(Paths.get(archivo.toURI()), reporte.columnas(), filasActuales);
            textoResultado.setText("Reporte exportado correctamente: " + archivo.getName());
        } catch (Exception ex) {
            textoResultado.setText("No se pudo exportar el CSV: " + ex.getMessage());
        }
    }

    /** Determina la plantilla y los nombres de campos que utiliza JasperReports. */
    private record PlantillaSeguimiento(String archivo, List<String> campos) { }

    private static PlantillaSeguimiento plantilla(ReporteSeguimientoService.Reporte reporte) {
        if (reporte == ReporteSeguimientoService.HORAS_POR_PRACTICA) {
            return new PlantillaSeguimiento("reporte_seguimiento_horas.jrxml",
                List.of("practica", "estudiante", "horas"));
        }
        if (reporte == ReporteSeguimientoService.PROMEDIO_EVALUACIONES) {
            return new PlantillaSeguimiento("reporte_seguimiento_promedios.jrxml",
                List.of("estudiante", "promedio"));
        }
        throw new IllegalArgumentException("No existe plantilla PDF para el reporte seleccionado.");
    }

    /** Mapea los resultados HQL sin modificar sus datos ni asumir IDs consecutivos. */
    private static List<java.util.Map<String, ?>> convertir(
            List<Object[]> registros, PlantillaSeguimiento plantilla) {
        List<java.util.Map<String, ?>> datos = new ArrayList<>();
        for (Object[] valores : registros) {
            if (valores.length != plantilla.campos().size()) {
                throw new IllegalStateException("La consulta HQL no coincide con la plantilla.");
            }
            java.util.Map<String, Object> fila = new java.util.LinkedHashMap<>();
            for (int i = 0; i < valores.length; i++) {
                fila.put(plantilla.campos().get(i),
                    valores[i] == null ? "" : valores[i].toString());
            }
            datos.add(fila);
        }
        return datos;
    }

    private static net.sf.jasperreports.engine.JasperPrint generarDocumento(
            ReporteSeguimientoService.Reporte reporte) throws Exception {
        PlantillaSeguimiento diseno = plantilla(reporte);
        return ni.edu.uam.practicasprofesionales.service.ReportePdfService.prepararReporte(
            diseno.archivo(), convertir(ReporteSeguimientoService.ejecutar(reporte), diseno));
    }

    /** Muestra exactamente las páginas generadas por JasperReports. */
    @FXML public void vistaPrevia() {
        var reporte = selectorReporte.getValue();
        if (reporte == null) {
            textoResultado.setText("Selecciona un reporte para abrir la vista previa.");
            return;
        }
        try {
            ReportePreviewController.abrir(
                tablaReporte.getScene().getWindow(), reporte.titulo(),
                () -> generarDocumento(reporte));
            textoResultado.setText("Abriendo vista previa institucional: " + reporte.titulo());
        } catch (Exception ex) {
            textoResultado.setText("No se pudo abrir la vista previa: " + ex.getMessage());
        }
    }

    /** Exportación directa del mismo diseño PDF que muestra la vista previa. */
    @FXML public void exportarPdf() {
        var reporte = selectorReporte.getValue();
        if (reporte == null) {
            textoResultado.setText("Selecciona un reporte para exportar PDF.");
            return;
        }
        var chooser = new FileChooser();
        chooser.setTitle("Guardar reporte PDF de seguimiento");
        chooser.setInitialFileName(reporte == ReporteSeguimientoService.HORAS_POR_PRACTICA
            ? "seguimiento_horas.pdf" : "seguimiento_promedios.pdf");
        chooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Documento PDF (*.pdf)", "*.pdf"));
        File archivo = chooser.showSaveDialog(tablaReporte.getScene().getWindow());
        if (archivo == null) return;
        textoResultado.setText("Generando PDF institucional...");
        FxDb.ejecutar(() -> {
            var documento = generarDocumento(reporte);
            ni.edu.uam.practicasprofesionales.service.ReportePdfService.exportarPdf(
                documento, archivo.toPath());
            return archivo.getName();
        }, nombre -> textoResultado.setText("PDF exportado correctamente: " + nombre),
            error -> textoResultado.setText("No se pudo exportar el PDF: "
                + error.getMessage()));
    }

    @FXML public void exportarExcel() {
        var reporte = selectorReporte.getValue();
        if (reporte == null) { textoResultado.setText("Selecciona un reporte."); return; }
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Exportar seguimiento a Excel");
        chooser.setInitialFileName("reporte_seguimiento.xlsx");
        chooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Libro Excel", "*.xlsx"));
        File archivo = chooser.showSaveDialog(tablaReporte.getScene().getWindow());
        if (archivo == null) return;
        textoResultado.setText("Generando Excel...");
        FxDb.ejecutar(() -> {
            List<List<String>> filas = new ArrayList<>();
            for (Object[] dato : ReporteSeguimientoService.ejecutar(reporte)) {
                List<String> fila = new ArrayList<>();
                for (Object valor : dato) fila.add(valor == null ? "" : valor.toString());
                filas.add(fila);
            }
            ReporteExcelService.generar(reporte.titulo(), reporte.columnas(),
                filas, archivo.toPath());
            return filas.size();
        }, cantidad -> textoResultado.setText("Excel generado: "
            + archivo.getName() + " (" + cantidad + " registros)"),
           error -> textoResultado.setText("Error Excel: " + error.getMessage()));
    }

}
