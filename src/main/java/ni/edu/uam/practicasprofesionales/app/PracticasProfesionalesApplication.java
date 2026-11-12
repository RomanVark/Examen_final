package ni.edu.uam.practicasprofesionales.app;

import ni.edu.uam.practicasprofesionales.persistence.JpaUtil;
import ni.edu.uam.practicasprofesionales.util.Alertas;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/** Clase principal. Sustituye HelloApplication de la plantilla original. */
public class PracticasProfesionalesApplication extends Application {
    @Override public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ni/edu/uam/practicasprofesionales/views/principal.fxml"));
        Scene scene = new Scene(loader.load(), 1380, 820);
        scene.getStylesheets().add(getClass().getResource("/ni/edu/uam/practicasprofesionales/styles/app.css").toExternalForm());
        stage.setTitle("Prácticas Profesionales | Sistema de Gestión Universitaria");
        stage.setScene(scene);
        stage.setMinWidth(1080);
        stage.setMinHeight(680);
        stage.setOnCloseRequest(e -> JpaUtil.shutdown());
        stage.show();
    }
    @Override public void stop() { JpaUtil.shutdown(); }
    public static void main(String[] args) { launch(args); }
}
