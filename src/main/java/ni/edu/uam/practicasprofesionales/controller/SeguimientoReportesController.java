package ni.edu.uam.practicasprofesionales.controller;

import jakarta.persistence.EntityManager;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;
import ni.edu.uam.practicasprofesionales.service.ExportadorCsv;
import ni.edu.uam.practicasprofesionales.service.ReporteSeguimientoService;
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
    private EntityManager entityManager;
    private List<List<String>> filasActuales = new ArrayList<>();

    public void configurar(EntityManager entityManager) {
        this.entityManager = entityManager;
        if (selectorReporte != null && selectorReporte.getValue() != null) cargarReporte();
    }

    @FXML public void initialize() {
        selectorReporte.setItems(FXCollections.observableArrayList(ReporteSeguimientoService.REPORTES));
        selectorReporte.getSelectionModel().selectFirst();
        textoResultado.setText("Seleccione un reporte cuando la conexión esté disponible.");
    }

    @FXML public void cargarReporte() {
        ReporteSeguimientoService.Reporte reporte = selectorReporte.getValue();
        if (reporte == null) return;
        if (entityManager == null) {
            textoResultado.setText("La conexión de persistencia todavía no está configurada.");
            return;
        }
        try {
            List<Object[]> resultado = ReporteSeguimientoService.ejecutar(entityManager, reporte);
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
        } catch (RuntimeException ex) {
            textoResultado.setText("No se pudo ejecutar el reporte: " + ex.getMessage());
        }
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
}
