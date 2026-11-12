package ni.edu.uam.practicasprofesionales.controller;

import ni.edu.uam.practicasprofesionales.service.ReporteHqlService;
import ni.edu.uam.practicasprofesionales.util.Alertas;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/** Reportes consultados con HQL y exportación CSV; los datos del CSV están en formato UTF-8. */
public class ReportesController {
    @FXML private ComboBox<ReporteHqlService.Reporte> selectorReporte;
    @FXML private TableView<List<String>> tablaReporte;
    @FXML private Label textoResultado;

    @FXML public void initialize() {
        selectorReporte.setItems(FXCollections.observableArrayList(ReporteHqlService.REPORTES));
        selectorReporte.getSelectionModel().selectFirst();
        cargar();
    }
    @FXML private void cargar() {
        var elegido = selectorReporte.getValue();
        if (elegido == null) return;
        try {
            tablaReporte.getColumns().clear();
            var resultados = ReporteHqlService.ejecutar(elegido);
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
        } catch (Exception ex) { Alertas.error(ex.getMessage()); }
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
}
