package ni.edu.uam.practicasprofesionales.controller;

import ni.edu.uam.practicasprofesionales.ui.Grupo;
import ni.edu.uam.practicasprofesionales.util.Alertas;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

/** Navegación central: cada pantalla es un archivo FXML editable con Scene Builder. */
public class PrincipalController {
    @FXML private StackPane areaContenido;
    @FXML private Label seccionActual;

    @FXML public void initialize() { mostrarInicio(); }
    @FXML public void mostrarInicio() { cargar("inicio.fxml", "Resumen general", null); }
    @FXML public void mostrarAcademico() { cargar("academico.fxml", "Carreras, estudiantes y tutores", null); }
    @FXML public void mostrarCatalogos() { cargar("crud.fxml", "Catálogos académicos y empresas", Grupo.CATALOGOS); }
    @FXML public void mostrarOperaciones() { cargar("crud.fxml", "Gestión de prácticas", Grupo.OPERACIONES); }
    @FXML public void mostrarReportes() { cargar("reportes.fxml", "Reportes HQL", null); }
    @FXML public void mostrarSeguimiento() {
        cargar("/ni/edu/uam/practicasprofesionales/seguimiento-reportes.fxml",
            "Reportes de seguimiento", null);
    }

    public void cerrar() { /* Cada consulta cierra su propia sesión. */ }

    private void cargar(String archivo, String titulo, Grupo grupo) {
        try {
            String recurso = archivo.startsWith("/") ? archivo :
                "/ni/edu/uam/practicasprofesionales/views/" + archivo;
            var url = getClass().getResource(recurso);
            if (url == null) throw new IllegalStateException("No se encontró el FXML: " + recurso);
            FXMLLoader loader = new FXMLLoader(url);
            Node vista = loader.load();
            Object controlador = loader.getController();
            if (grupo != null) ((CrudController) controlador).configurar(grupo);
            areaContenido.getChildren().setAll(vista);
            seccionActual.setText(titulo);
        } catch (Exception ex) {
            Alertas.error("No se pudo abrir la vista: " + ex.getMessage() +
                "\nRevisa la vista FXML y, si necesita datos, la conexión PostgreSQL.");
        }
    }
}
