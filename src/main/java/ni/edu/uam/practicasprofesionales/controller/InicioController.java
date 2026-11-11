package ni.edu.uam.practicasprofesionales.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import ni.edu.uam.practicasprofesionales.service.AcademicoService;

public final class InicioController {
 @FXML private Label totalCarreras;
 @FXML private Label totalEstudiantes;
 @FXML private Label totalTutores;
 @FXML private Label totalActivos;
 @FXML private Label estado;
 @FXML private ListView<String> distribucion;
 private final AcademicoService service=new AcademicoService();
 @FXML public void initialize(){actualizar();}
 @FXML private void actualizar(){
  try {
   var carreras=service.carreras();
   var estudiantes=service.estudiantes();
   var tutores=service.tutores();
   totalCarreras.setText(String.valueOf(carreras.size()));
   totalEstudiantes.setText(String.valueOf(estudiantes.size()));
   totalTutores.setText(String.valueOf(tutores.size()));
   totalActivos.setText(String.valueOf(estudiantes.stream().filter(e->e.isActivo()).count()));
   var rows=service.estudiantesPorCarrera();
   distribucion.setItems(FXCollections.observableArrayList(rows.stream()
    .map(r->r[0]+" — "+r[1]+" estudiante(s)").toList()));
   estado.setText("Datos recuperados mediante Hibernate y HQL.");
  } catch (Exception ex) {
   estado.setText("No se pudo consultar PostgreSQL: "+causa(ex));
  }
 }
 private static String causa(Throwable e){while(e.getCause()!=null)e=e.getCause();return e.getMessage();}
}
