package ni.edu.uam.practicasprofesionales.controller;

import ni.edu.uam.practicasprofesionales.util.FxDb;
import ni.edu.uam.practicasprofesionales.web.UsuarioApi;
import ni.edu.uam.practicasprofesionales.web.UsuariosRestClient;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

/** Integra dos llamadas HTTP sin bloquear el hilo JavaFX. */
public final class ServiciosWebController {
    @FXML private TextField campoId;
    @FXML private TableView<UsuarioApi> tablaUsuarios;
    @FXML private TableColumn<UsuarioApi, Number> columnaId;
    @FXML private TableColumn<UsuarioApi, String> columnaNombre;
    @FXML private TableColumn<UsuarioApi, String> columnaUsuario;
    @FXML private TableColumn<UsuarioApi, String> columnaCorreo;
    @FXML private TableColumn<UsuarioApi, String> columnaCiudad;
    @FXML private Label estadoHttp;
    @FXML private Label detalle;
    @FXML private Button btnListar;
    @FXML private Button btnDetalle;
    @FXML private ProgressIndicator progreso;

    private final UsuariosRestClient servicio = new UsuariosRestClient();

    @FXML public void initialize() {
        columnaId.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().id()));
        columnaNombre.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().nombre()));
        columnaUsuario.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().usuario()));
        columnaCorreo.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().correo()));
        columnaCiudad.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().ciudad()));
        tablaUsuarios.getSelectionModel().selectedItemProperty().addListener(
            (obs, anterior, nuevo) -> {
                if (nuevo != null) {
                    campoId.setText(String.valueOf(nuevo.id()));
                    detalle.setText(describir(nuevo));
                }
            });
        progreso.setVisible(false);
        estadoHttp.setText("API de pruebas JSONPlaceholder; datos simulados, no datos universitarios.");
    }

    @FXML private void listarUsuarios() {
        ocupado(true);
        estadoHttp.setText("GET /users — consultando servicio HTTPS...");
        FxDb.ejecutar(servicio::listar, usuarios -> {
            ocupado(false);
            tablaUsuarios.setItems(FXCollections.observableArrayList(usuarios));
            estadoHttp.setText(usuarios.isEmpty()
                ? "HTTP 200: consulta correcta, sin resultados."
                : "HTTP 200: " + usuarios.size() + " usuarios obtenidos mediante JSON.");
            if (usuarios.isEmpty()) detalle.setText("No se recibieron usuarios.");
        }, ex -> error("GET /users", ex));
    }

    @FXML private void consultarDetalle() {
        final int id;
        try {
            id = Integer.parseInt(campoId.getText().strip());
            if (id <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            estadoHttp.setText("Validación: introduce un ID entero mayor que cero.");
            return;
        }
        ocupado(true);
        estadoHttp.setText("GET /users/" + id + " — consultando...");
        FxDb.ejecutar(() -> servicio.detalle(id), usuario -> {
            ocupado(false);
            tablaUsuarios.setItems(FXCollections.observableArrayList(List.of(usuario)));
            detalle.setText(describir(usuario));
            estadoHttp.setText("HTTP 200: detalle recibido para ID " + id + ".");
        }, ex -> error("GET /users/" + id, ex));
    }

    private void error(String operacion, Throwable ex) {
        ocupado(false);
        Throwable causa = ex.getCause() == null ? ex : ex.getCause();
        estadoHttp.setText(operacion + " — " + causa.getMessage());
        detalle.setText("No se pudieron recuperar datos. Verifica internet o utiliza otro ID.");
    }

    private void ocupado(boolean valor) {
        btnListar.setDisable(valor);
        btnDetalle.setDisable(valor);
        progreso.setVisible(valor);
    }

    private static String describir(UsuarioApi u) {
        return "ID: " + u.id() + " | Nombre: " + u.nombre()
            + " | Usuario: " + u.usuario() + " | Correo: " + u.correo()
            + " | Ciudad: " + u.ciudad();
    }
}
