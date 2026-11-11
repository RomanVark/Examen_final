package ni.edu.uam.practicasprofesionales.controller;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import ni.edu.uam.practicasprofesionales.model.academico.*;
import ni.edu.uam.practicasprofesionales.service.AcademicoService;
import java.util.function.Function;

/** Controlador de tres CRUD académicos; formulario y tabla desacoplados de Hibernate. */
public final class AcademicoController {
 private final AcademicoService service=new AcademicoService();
 @FXML private TableView<Carrera> tablaCarreras;
 @FXML private TableView<Estudiante> tablaEstudiantes;
 @FXML private TableView<TutorAcademico> tablaTutores;
 @FXML private TextField carreraCodigo,carreraNombre,carreraFacultad;
 @FXML private CheckBox carreraActiva;
 @FXML private TextField estudianteCarnet,estudianteNombres,estudianteApellidos,estudianteCorreo,estudianteTelefono,buscarEstudiante;
 @FXML private ComboBox<Carrera> estudianteCarrera;
 @FXML private CheckBox estudianteActivo;
 @FXML private TextField tutorNombres,tutorApellidos,tutorCorreo,tutorEspecialidad,buscarTutor;
 @FXML private CheckBox tutorActivo;
 @FXML private Label estado;

 @FXML public void initialize(){
  columna(tablaCarreras,"Código",Carrera::getCodigo);
  columna(tablaCarreras,"Carrera",Carrera::getNombre);
  columna(tablaCarreras,"Facultad",Carrera::getFacultad);
  columna(tablaCarreras,"Activa",c->c.isActiva()?"Sí":"No");
  columna(tablaEstudiantes,"Carné",Estudiante::getCarnet);
  columna(tablaEstudiantes,"Estudiante",e->e.getNombres()+" "+e.getApellidos());
  columna(tablaEstudiantes,"Correo",Estudiante::getCorreo);
  columna(tablaEstudiantes,"Carrera",e->e.getCarrera().getNombre());
  columna(tablaEstudiantes,"Activo",e->e.isActivo()?"Sí":"No");
  columna(tablaTutores,"Tutor",t->t.getNombres()+" "+t.getApellidos());
  columna(tablaTutores,"Correo",TutorAcademico::getCorreo);
  columna(tablaTutores,"Especialidad",TutorAcademico::getEspecialidad);
  columna(tablaTutores,"Activo",t->t.isActivo()?"Sí":"No");
  tablaCarreras.getSelectionModel().selectedItemProperty().addListener((o,old,v)->{if(v!=null)editarCarrera(v);});
  tablaEstudiantes.getSelectionModel().selectedItemProperty().addListener((o,old,v)->{if(v!=null)editarEstudiante(v);});
  tablaTutores.getSelectionModel().selectedItemProperty().addListener((o,old,v)->{if(v!=null)editarTutor(v);});
  buscarEstudiante.textProperty().addListener((o,old,v)->cargarEstudiantes());
  buscarTutor.textProperty().addListener((o,old,v)->cargarTutores());
  recargar();
 }
 private static <T> void columna(TableView<T> tabla,String titulo,Function<T,String> valor){
  TableColumn<T,String> col=new TableColumn<>(titulo);
  col.setCellValueFactory(d->new ReadOnlyStringWrapper(valor.apply(d.getValue())));
  col.setPrefWidth(150);
  tabla.getColumns().add(col);
 }
 @FXML private void recargar(){
  try{
   tablaCarreras.setItems(FXCollections.observableArrayList(service.carreras()));
   estudianteCarrera.setItems(FXCollections.observableArrayList(service.carreras()));
   cargarEstudiantes();
   cargarTutores();
   estado.setText("Módulo académico actualizado.");
  }catch(Exception e){error(e);}
 }
 private void cargarEstudiantes(){
  try{tablaEstudiantes.setItems(FXCollections.observableArrayList(service.buscarEstudiantes(buscarEstudiante.getText()==null?"":buscarEstudiante.getText())));}
  catch(Exception e){error(e);}
 }
 private void cargarTutores(){
  try{tablaTutores.setItems(FXCollections.observableArrayList(service.buscarTutores(buscarTutor.getText()==null?"":buscarTutor.getText())));}
  catch(Exception e){error(e);}
 }
 private void editarCarrera(Carrera c){
  carreraCodigo.setText(c.getCodigo());carreraNombre.setText(c.getNombre());
  carreraFacultad.setText(c.getFacultad());carreraActiva.setSelected(c.isActiva());
 }
 private void editarEstudiante(Estudiante e){
  estudianteCarnet.setText(e.getCarnet());estudianteNombres.setText(e.getNombres());
  estudianteApellidos.setText(e.getApellidos());estudianteCorreo.setText(e.getCorreo());
  estudianteTelefono.setText(e.getTelefono());
  estudianteCarrera.getItems().stream().filter(c->c.getId().equals(e.getCarrera().getId()))
    .findFirst().ifPresent(estudianteCarrera::setValue);
  estudianteActivo.setSelected(e.isActivo());
 }
 private void editarTutor(TutorAcademico t){
  tutorNombres.setText(t.getNombres());tutorApellidos.setText(t.getApellidos());
  tutorCorreo.setText(t.getCorreo());tutorEspecialidad.setText(t.getEspecialidad());
  tutorActivo.setSelected(t.isActivo());
 }
 @FXML private void guardarCarrera(){
  try{
   Carrera c=tablaCarreras.getSelectionModel().getSelectedItem();
   if(c==null)c=new Carrera();
   c.setCodigo(carreraCodigo.getText());c.setNombre(carreraNombre.getText());
   c.setFacultad(carreraFacultad.getText());c.setActiva(carreraActiva.isSelected());
   service.guardar(c);limpiarCarrera();recargar();estado.setText("Carrera guardada.");
  }catch(Exception ex){error(ex);}
 }
 @FXML private void guardarEstudiante(){
  try{
   Estudiante e=tablaEstudiantes.getSelectionModel().getSelectedItem();
   if(e==null)e=new Estudiante();
   e.setCarnet(estudianteCarnet.getText());e.setNombres(estudianteNombres.getText());
   e.setApellidos(estudianteApellidos.getText());e.setCorreo(estudianteCorreo.getText());
   e.setTelefono(estudianteTelefono.getText());e.setCarrera(estudianteCarrera.getValue());
   e.setActivo(estudianteActivo.isSelected());
   service.guardar(e);limpiarEstudiante();recargar();estado.setText("Estudiante guardado.");
  }catch(Exception ex){error(ex);}
 }
 @FXML private void guardarTutor(){
  try{
   TutorAcademico t=tablaTutores.getSelectionModel().getSelectedItem();
   if(t==null)t=new TutorAcademico();
   t.setNombres(tutorNombres.getText());t.setApellidos(tutorApellidos.getText());
   t.setCorreo(tutorCorreo.getText());t.setEspecialidad(tutorEspecialidad.getText());
   t.setActivo(tutorActivo.isSelected());
   service.guardar(t);limpiarTutor();recargar();estado.setText("Tutor guardado.");
  }catch(Exception ex){error(ex);}
 }
 @FXML private void eliminarCarrera(){
  Carrera c=tablaCarreras.getSelectionModel().getSelectedItem();
  if(c==null)return;
  if(!confirmar("Eliminar carrera",c.toString()))return;
  try{service.eliminarCarrera(c.getId());limpiarCarrera();recargar();}catch(Exception e){error(e);}
 }
 @FXML private void eliminarEstudiante(){
  Estudiante e=tablaEstudiantes.getSelectionModel().getSelectedItem();
  if(e==null)return;
  if(!confirmar("Eliminar estudiante",e.toString()))return;
  try{service.eliminarEstudiante(e.getId());limpiarEstudiante();recargar();}catch(Exception ex){error(ex);}
 }
 @FXML private void eliminarTutor(){
  TutorAcademico t=tablaTutores.getSelectionModel().getSelectedItem();
  if(t==null)return;
  if(!confirmar("Eliminar tutor",t.toString()))return;
  try{service.eliminarTutor(t.getId());limpiarTutor();recargar();}catch(Exception ex){error(ex);}
 }
 @FXML private void limpiarCarrera(){
  tablaCarreras.getSelectionModel().clearSelection();
  carreraCodigo.clear();carreraNombre.clear();carreraFacultad.clear();carreraActiva.setSelected(true);
 }
 @FXML private void limpiarEstudiante(){
  tablaEstudiantes.getSelectionModel().clearSelection();
  estudianteCarnet.clear();estudianteNombres.clear();estudianteApellidos.clear();
  estudianteCorreo.clear();estudianteTelefono.clear();estudianteCarrera.getSelectionModel().clearSelection();
  estudianteActivo.setSelected(true);
 }
 @FXML private void limpiarTutor(){
  tablaTutores.getSelectionModel().clearSelection();
  tutorNombres.clear();tutorApellidos.clear();tutorCorreo.clear();tutorEspecialidad.clear();tutorActivo.setSelected(true);
 }
 private boolean confirmar(String titulo,String detalle){
  Alert alerta=new Alert(Alert.AlertType.CONFIRMATION,"¿Desea eliminar "+detalle+"?",ButtonType.YES,ButtonType.NO);
  alerta.setTitle(titulo);
  return alerta.showAndWait().orElse(ButtonType.NO)==ButtonType.YES;
 }
 private void error(Exception e){
  Throwable t=e;
  while(t.getCause()!=null)t=t.getCause();
  estado.setText("Error: "+t.getMessage());
  Alert alerta=new Alert(Alert.AlertType.ERROR,t.getMessage(),ButtonType.OK);
  alerta.setHeaderText("No se pudo completar la operación");
  alerta.showAndWait();
 }
}
