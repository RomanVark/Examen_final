package ni.edu.uam.practicasprofesionales.controller;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Dialog;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import ni.edu.uam.practicasprofesionales.service.ExportadorCsv;
import ni.edu.uam.practicasprofesionales.service.ReporteExcelService;
import ni.edu.uam.practicasprofesionales.service.ReporteSeguimientoService;
import ni.edu.uam.practicasprofesionales.util.FxDb;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    /** Previsualiza los datos del reporte seleccionado antes de exportarlo. */
    @FXML public void vistaPrevia() {
        var reporte = selectorReporte.getValue();
        if (reporte == null) { textoResultado.setText("Selecciona un reporte."); return; }
        Dialog<Void> dialogo = new Dialog<>();
        dialogo.setTitle("Vista previa de seguimiento");
        dialogo.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        Label titulo = new Label(reporte.titulo());
        Label fecha = new Label("Generado: "
            + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        Label total = new Label("Consultando PostgreSQL...");
        TableView<List<String>> vista = new TableView<>();
        vista.setPrefSize(800, 450);
        for (int i = 0; i < reporte.columnas().size(); i++) {
            final int indice = i;
            TableColumn<List<String>, String> columna =
                new TableColumn<>(reporte.columnas().get(i));
            columna.setCellValueFactory(x ->
                new ReadOnlyStringWrapper(x.getValue().get(indice)));
            columna.setPrefWidth(235);
            vista.getColumns().add(columna);
        }
        VBox contenido = new VBox(12, titulo, fecha, total, vista);
        dialogo.getDialogPane().setContent(contenido);
        FxDb.ejecutar(() -> ReporteSeguimientoService.ejecutar(reporte), datos -> {
            List<List<String>> filas = new ArrayList<>();
            for (Object[] registro : datos) {
                List<String> fila = new ArrayList<>();
                for (Object valor : registro) fila.add(valor == null ? "" : valor.toString());
                filas.add(fila);
            }
            vista.setItems(FXCollections.observableArrayList(filas));
            total.setText(filas.size() + " registros (vista previa de datos)");
        }, error -> total.setText("Error de consulta: " + error.getMessage()));
        dialogo.show();
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
