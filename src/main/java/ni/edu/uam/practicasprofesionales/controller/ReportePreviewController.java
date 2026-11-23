package ni.edu.uam.practicasprofesionales.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import net.sf.jasperreports.engine.JasperPrint;
import ni.edu.uam.practicasprofesionales.service.ReportePdfService;
import ni.edu.uam.practicasprofesionales.util.FxDb;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.concurrent.Callable;

/**
 * Visor JavaFX de las páginas verdaderas del JasperPrint: zoom,
 * navegación, exportación PDF e impresión del mismo documento.
 */
public final class ReportePreviewController {
    @FXML private Label titulo;
    @FXML private Label estado;
    @FXML private Label numeroPagina;
    @FXML private Label etiquetaZoom;
    @FXML private ImageView paginaImagen;
    @FXML private ProgressIndicator progreso;
    @FXML private Button anterior;
    @FXML private Button siguiente;
    @FXML private Button reducirZoom;
    @FXML private Button ampliarZoom;
    @FXML private Button exportarPdf;
    @FXML private Button imprimir;

    private Stage ventana;
    private JasperPrint documento;
    private int paginaActual = 0;
    private float zoom = 1.0f;
    private long revision;
    private boolean cerrado;

    public static void abrir(
            Window propietario, String nombreReporte, Callable<JasperPrint> generador)
            throws Exception {
        FXMLLoader loader = new FXMLLoader(ReportePreviewController.class.getResource(
            "/ni/edu/uam/practicasprofesionales/views/reporte-preview.fxml"));
        Parent vista = loader.load();
        Stage ventana = new Stage();
        ventana.initOwner(propietario);
        ventana.initModality(Modality.NONE);
        ventana.setTitle("Vista previa PDF — " + nombreReporte);
        ventana.setScene(new Scene(vista, 1040, 740));
        ventana.setMinWidth(810);
        ventana.setMinHeight(550);
        ReportePreviewController controlador = loader.getController();
        controlador.configurar(ventana, nombreReporte, generador);
        ventana.show();
    }

    private void configurar(
            Stage stage, String nombreReporte, Callable<JasperPrint> generador) {
        ventana = stage;
        titulo.setText(nombreReporte);
        paginaImagen.setPreserveRatio(true);
        paginaImagen.setSmooth(true);
        stage.setOnHidden(e -> { cerrado = true; revision++; });
        estado.setText("Preparando el documento JasperReports...");
        actualizarControles();
        progreso.setVisible(true);
        FxDb.ejecutar(generador, resultado -> {
            if (cerrado) return;
            documento = resultado;
            paginaActual = 0;
            if (resultado.getPages().isEmpty()) {
                progreso.setVisible(false);
                estado.setText("No hay páginas para mostrar.");
                actualizarControles();
            } else {
                mostrarPagina();
            }
        }, fallo -> {
            if (cerrado) return;
            progreso.setVisible(false);
            estado.setText("Error al generar vista previa: " + fallo.getMessage());
            actualizarControles();
        });
    }

    private void mostrarPagina() {
        if (cerrado || documento == null || documento.getPages().isEmpty()) return;
        int numero = paginaActual;
        float escala = zoom;
        long solicitud = ++revision;
        progreso.setVisible(true);
        estado.setText("Renderizando página " + (numero + 1) + "...");
        actualizarControles();
        FxDb.ejecutar(() -> ReportePdfService.renderizarPagina(documento, numero, escala),
            imagen -> {
                if (cerrado || solicitud != revision) return;
                Image imagenFx = new Image(new ByteArrayInputStream(imagen));
                paginaImagen.setImage(imagenFx);
                progreso.setVisible(false);
                estado.setText("Vista real de JasperReports · Lista para exportar o imprimir.");
                actualizarControles();
            }, fallo -> {
                if (cerrado || solicitud != revision) return;
                progreso.setVisible(false);
                estado.setText("Error al dibujar el documento: " + fallo.getMessage());
                actualizarControles();
            });
    }

    private void actualizarControles() {
        boolean listo = documento != null && !documento.getPages().isEmpty();
        boolean ocupado = progreso.isVisible();
        int paginas = listo ? documento.getPages().size() : 0;
        numeroPagina.setText("Página " + (listo ? paginaActual + 1 : 0) + " de " + paginas);
        etiquetaZoom.setText(Math.round(zoom * 100) + " %");
        anterior.setDisable(!listo || ocupado || paginaActual <= 0);
        siguiente.setDisable(!listo || ocupado || paginaActual + 1 >= paginas);
        reducirZoom.setDisable(!listo || ocupado || zoom <= 0.6f);
        ampliarZoom.setDisable(!listo || ocupado || zoom >= 1.6f);
        exportarPdf.setDisable(!listo);
        imprimir.setDisable(!listo);
    }

    @FXML private void paginaAnterior() { paginaActual--; mostrarPagina(); }
    @FXML private void paginaSiguiente() { paginaActual++; mostrarPagina(); }
    @FXML private void acercar() { zoom = Math.min(1.6f, zoom + 0.2f); mostrarPagina(); }
    @FXML private void alejar() { zoom = Math.max(0.6f, zoom - 0.2f); mostrarPagina(); }

    @FXML private void guardarPdf() {
        if (documento == null) return;
        FileChooser selector = new FileChooser();
        selector.setTitle("Guardar PDF del documento previsualizado");
        selector.setInitialFileName("reporte_practicas_profesionales.pdf");
        selector.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("PDF", "*.pdf"));
        File destino = selector.showSaveDialog(ventana);
        if (destino == null) return;
        exportarPdf.setDisable(true);
        estado.setText("Exportando PDF...");
        FxDb.ejecutar(() -> {
            ReportePdfService.exportarPdf(documento, destino.toPath());
            return destino.getName();
        }, nombre -> {
            if (cerrado) return;
            estado.setText("PDF guardado: " + nombre);
            actualizarControles();
        }, fallo -> {
            if (cerrado) return;
            estado.setText("Error al guardar: " + fallo.getMessage());
            actualizarControles();
        });
    }

    @FXML private void imprimirReporte() {
        if (documento == null) return;
        imprimir.setDisable(true);
        estado.setText("Abriendo el diálogo de impresión...");
        FxDb.ejecutar(() -> {
            ReportePdfService.imprimir(documento);
            return null;
        }, ok -> {
            if (cerrado) return;
            estado.setText("Diálogo de impresión finalizado.");
            actualizarControles();
        }, fallo -> {
            if (cerrado) return;
            estado.setText("No se pudo imprimir: " + fallo.getMessage());
            actualizarControles();
        });
    }
}
