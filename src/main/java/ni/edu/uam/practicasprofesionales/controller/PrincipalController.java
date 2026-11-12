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
    @FXML public void mostrarCatalogos() { cargar("crud.fxml", "Catálogos académicos y empresas", Grupo.CATALOGOS); }
    @FXML public void mostrarOperaciones() { cargar("crud.fxml", "Gestión de prácticas", Grupo.OPERACIONES); }
    @FXML public void mostrarReportes() { cargar("reportes.fxml", "Reportes HQL", null); }

    private void cargar(String archivo, String titulo, Grupo grupo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ni/edu/uam/practicasprofesionales/views/" + archivo));
            Node vista = loader.load();
            if (grupo != null) ((CrudController) loader.getController()).configurar(grupo);
            areaContenido.getChildren().setAll(vista);
            seccionActual.setText(titulo);
        } catch (Exception ex) {
            Alertas.error("No se pudo abrir la vista: " + ex.getMessage() +
                "\nRevisa la conexión PostgreSQL y las variables DB_URL, DB_USER y DB_PASSWORD.");
        }
    }
}
