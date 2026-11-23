package ni.edu.uam.practicasprofesionales.controller;

import ni.edu.uam.practicasprofesionales.service.ReporteHqlService;
import ni.edu.uam.practicasprofesionales.service.ReportePdfService;
import ni.edu.uam.practicasprofesionales.service.ReporteExcelService;
import ni.edu.uam.practicasprofesionales.util.Alertas;
import ni.edu.uam.practicasprofesionales.util.FxDb;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Dialog;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableRow;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.control.Separator;

import javafx.stage.FileChooser;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;

/** Reportes consultados con HQL y exportación CSV; los datos del CSV están en formato UTF-8. */
public class ReportesController {
    @FXML private ComboBox<ReporteHqlService.Reporte> selectorReporte;
    @FXML private TableView<List<String>> tablaReporte;
    @FXML private Label textoResultado;
    @FXML private Button btnExportarPdf;

    @FXML public void initialize() {
        selectorReporte.setItems(FXCollections.observableArrayList(ReporteHqlService.REPORTES));
        selectorReporte.getSelectionModel().selectFirst();
        cargar();
    }
    @FXML private void cargar() {
        var elegido = selectorReporte.getValue();
        if (elegido == null) return;
        FxDb.ejecutar(() -> ReporteHqlService.ejecutar(elegido), resultados -> {
            if (selectorReporte.getValue() != elegido) return;
            tablaReporte.getColumns().clear();
            List<List<String>> filas = new ArrayList<>();
            for (Object[] fila : resultados) {
                List<String> valores = new ArrayList<>();
                for (Object campo : fila) valores.add(campo == null ? "" : campo.toString());
                filas.add(valores);
            }
            for (int i = 0; i < elegido.columnas().size(); i++) {
                final int index = i;
                TableColumn<List<String>, String> columna = new TableColumn<>(elegido.columnas().get(i));
                columna.setCellValueFactory(d -> new ReadOnlyStringWrapper(d.getValue().get(index)));
                columna.setPrefWidth(230);
                tablaReporte.getColumns().add(columna);
            }
            tablaReporte.setItems(FXCollections.observableArrayList(filas));
            textoResultado.setText(filas.size() + " resultado(s) · Consulta HQL sobre entidades relacionadas");
        }, error -> Alertas.error(error.getMessage()));
    }
    @FXML private void exportarCsv() {
        var reporte = selectorReporte.getValue();
        if (reporte == null) return;
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Guardar reporte CSV");
        chooser.setInitialFileName("reporte_practicas.csv");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV (*.csv)", "*.csv"));
        var archivo = chooser.showSaveDialog(tablaReporte.getScene().getWindow());
        if (archivo == null) return;
        try {
            List<String> lineas = new ArrayList<>();
            lineas.add(csv(reporte.columnas()));
            for (List<String> fila : tablaReporte.getItems()) lineas.add(csv(fila));
            Files.write(Path.of(archivo.toURI()), lineas, StandardCharsets.UTF_8);
            Alertas.informacion("Reporte exportado: " + archivo.getAbsolutePath());
        } catch (Exception ex) { Alertas.error("No se pudo exportar: " + ex.getMessage()); }
    }
    private static String csv(List<String> fila) {
        return fila.stream().map(s -> "\"" + s.replace("\"", "\"\"") + "\"")
            .collect(Collectors.joining(","));
    }

    /** JasperReports usa las cuatro plantillas JRXML provenientes de master. */
    private record PlantillaPdf(String archivo, List<String> campos) { }

    private static PlantillaPdf plantillaPara(ReporteHqlService.Reporte reporte) {
        return switch (reporte.titulo()) {
            case "Prácticas por estudiante y empresa" ->
                new PlantillaPdf("reporte_practicas.jrxml",
                    List.of("id", "estudiante", "empresa", "estado"));
            case "Horas registradas por práctica" ->
                new PlantillaPdf("reporte_horas.jrxml",
                    List.of("practica", "estudiante", "horas"));
            case "Postulaciones por oferta" ->
                new PlantillaPdf("reporte_postulaciones.jrxml",
                    List.of("codigo", "oferta", "postulaciones"));
            case "Promedio de evaluación por estudiante" ->
                new PlantillaPdf("reporte_promedios.jrxml",
                    List.of("estudiante", "promedio"));
            default -> throw new IllegalArgumentException(
                "No existe plantilla PDF para: " + reporte.titulo());
        };
    }

    @FXML private void exportarPdf() {
        var reporte = selectorReporte.getValue();
        if (reporte == null) {
            Alertas.error("Selecciona un reporte antes de exportar a PDF.");
            return;
        }

        final PlantillaPdf plantilla;
        try {
            plantilla = plantillaPara(reporte);
        } catch (IllegalArgumentException ex) {
            Alertas.error(ex.getMessage());
            return;
        }

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Guardar reporte PDF");
        chooser.setInitialFileName(plantilla.archivo().replace(".jrxml", ".pdf"));
        chooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Archivo PDF", "*.pdf"));
        var archivo = chooser.showSaveDialog(tablaReporte.getScene().getWindow());
        if (archivo == null) return;

        btnExportarPdf.setDisable(true);
        textoResultado.setText("Generando reporte PDF...");

        // Se consulta PostgreSQL y compila JasperReports fuera del hilo JavaFX.
        FxDb.ejecutar(() -> {
            List<Object[]> resultados = ReporteHqlService.ejecutar(reporte);
            List<Map<String, ?>> datos = convertirDatos(plantilla, resultados);
            ReportePdfService.generarReporte(
                plantilla.archivo(), datos, archivo.getAbsolutePath());
            return archivo.getAbsolutePath();
        }, destino -> {
            btnExportarPdf.setDisable(false);
            textoResultado.setText("Reporte PDF generado: " + archivo.getName());
            Alertas.informacion("Reporte PDF generado correctamente:\n" + destino);
        }, error -> {
            btnExportarPdf.setDisable(false);
            textoResultado.setText("No se pudo generar el reporte PDF.");
            Alertas.error("Error de exportación PDF: " + error.getMessage());
        });
    }


    /** Abre un visor de las páginas reales del PDF JasperReports. */
    @FXML private void vistaPrevia() {
        var reporte = selectorReporte.getValue();
        if (reporte == null) {
            Alertas.error("Selecciona un reporte antes de previsualizar.");
            return;
        }
        final PlantillaPdf plantilla;
        try {
            plantilla = plantillaPara(reporte);
        } catch (IllegalArgumentException ex) {
            Alertas.error(ex.getMessage());
            return;
        }
        try {
            ReportePreviewController.abrir(
                tablaReporte.getScene().getWindow(), reporte.titulo(), () -> {
                    List<Object[]> resultados = ReporteHqlService.ejecutar(reporte);
                    return ReportePdfService.prepararReporte(
                        plantilla.archivo(), convertirDatos(plantilla, resultados));
                });
        } catch (Exception ex) {
            Alertas.error("No se pudo abrir el visor: " + ex.getMessage());
        }
    }

    /** La vista previa y la exportación usan el mismo mapeo de campos. */
    private static List<Map<String, ?>> convertirDatos(
            PlantillaPdf plantilla, List<Object[]> resultados) {
        List<Map<String, ?>> datos = new ArrayList<>();
        for (Object[] fila : resultados) {
            if (fila.length != plantilla.campos().size()) {
                throw new IllegalStateException("Campos HQL incompatibles con la plantilla: "
                    + plantilla.archivo());
            }
            Map<String, Object> mapa = new LinkedHashMap<>();
            for (int i = 0; i < fila.length; i++) {
                mapa.put(plantilla.campos().get(i),
                    fila[i] == null ? "" : fila[i].toString());
            }
            datos.add(mapa);
        }
        return datos;
    }

    @FXML private void exportarExcel() {
        var reporte = selectorReporte.getValue();
        if (reporte == null) { Alertas.error("Selecciona un reporte."); return; }
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Guardar reporte Excel");
        chooser.setInitialFileName("reporte_" + reporte.titulo()
            .toLowerCase(java.util.Locale.ROOT).replaceAll("[^a-z0-9]+", "_") + ".xlsx");
        chooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Libro Excel (*.xlsx)", "*.xlsx"));
        var archivo = chooser.showSaveDialog(tablaReporte.getScene().getWindow());
        if (archivo == null) return;
        textoResultado.setText("Generando archivo Excel...");
        FxDb.ejecutar(() -> {
            List<Object[]> datos = ReporteHqlService.ejecutar(reporte);
            List<List<String>> filas = new ArrayList<>();
            for (Object[] dato : datos) {
                List<String> fila = new ArrayList<>();
                for (Object valor : dato) fila.add(valor == null ? "" : valor.toString());
                filas.add(fila);
            }
            ReporteExcelService.generar(reporte.titulo(), reporte.columnas(),
                filas, archivo.toPath());
            return filas.size();
        }, cantidad -> {
            textoResultado.setText("Excel generado: " + cantidad + " registros.");
            Alertas.informacion("Excel XLSX guardado correctamente:\n" + archivo.getAbsolutePath());
        }, fallo -> {
            textoResultado.setText("Error al exportar Excel.");
            Alertas.error("No se pudo exportar Excel: " + fallo.getMessage());
        });
    }

}
