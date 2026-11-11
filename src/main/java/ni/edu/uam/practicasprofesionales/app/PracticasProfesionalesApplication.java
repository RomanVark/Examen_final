package ni.edu.uam.practicasprofesionales.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ni.edu.uam.practicasprofesionales.persistence.JpaUtil;

public class PracticasProfesionalesApplication extends Application {
 @Override public void start(Stage stage) throws Exception {
  FXMLLoader loader = new FXMLLoader(getClass().getResource("/ni/edu/uam/practicasprofesionales/views/principal.fxml"));
  Scene scene = new Scene(loader.load(), 1200, 760);
  scene.getStylesheets().add(getClass().getResource("/ni/edu/uam/practicasprofesionales/styles/app.css").toExternalForm());
  stage.setTitle("Prácticas profesionales | Módulo académico");
  stage.setMinWidth(950);
  stage.setMinHeight(650);
  stage.setScene(scene);
  stage.show();
 }
 @Override public void stop() { JpaUtil.close(); }
 public static void main(String[] args) { launch(args); }
}
