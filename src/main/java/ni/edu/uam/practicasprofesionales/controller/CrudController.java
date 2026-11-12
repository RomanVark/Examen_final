package ni.edu.uam.practicasprofesionales.controller;

import ni.edu.uam.practicasprofesionales.repository.JpaRepository;
import ni.edu.uam.practicasprofesionales.model.alianzas.Empresa;
import ni.edu.uam.practicasprofesionales.ui.EntityCatalog;
import ni.edu.uam.practicasprofesionales.ui.EntityDefinition;
import ni.edu.uam.practicasprofesionales.ui.FieldSpec;
import ni.edu.uam.practicasprofesionales.ui.Grupo;
import ni.edu.uam.practicasprofesionales.util.Alertas;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/** Formulario + tabla CRUD reutilizables para las 12 entidades. El contenedor se edita en Scene Builder. */
public class CrudController {
    @FXML private ComboBox<EntityDefinition<?>> selectorEntidad;
    @FXML private TextField filtro;
    @FXML private TableView<Object> tabla;
    @FXML private VBox camposFormulario;
    @FXML private Label etiquetaCantidad;
    @FXML private Label etiquetaFormulario;
    @FXML private Label etiquetaAyuda;
    @FXML private Button btnEliminar;

    private EntityDefinition<?> actual;
    private Object editando;
    private final Map<String, Control> controles = new HashMap<>();
    private List<Object> todos = List.of();

    @FXML public void initialize() {
        filtro.textProperty().addListener((obs, oldText, newText) -> aplicarFiltro());
        tabla.getSelectionModel().selectedItemProperty().addListener((obs, oldItem, newItem) -> {
            if (newItem != null) cargarSeleccion(newItem);
        });
        btnEliminar.setDisable(true);
    }

    public void configurar(Grupo grupo) {
        selectorEntidad.setItems(FXCollections.observableArrayList(EntityCatalog.porGrupo(grupo)));
        selectorEntidad.getSelectionModel().selectFirst();
        cambiarEntidad();
    }

    @FXML private void cambiarEntidad() {
        actual = selectorEntidad.getValue();
        if (actual == null) return;
        filtro.clear();
        tabla.getColumns().clear();
        TableColumn<Object, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(d -> new ReadOnlyStringWrapper(texto(d.getValue(), "id")));
        idCol.setPrefWidth(70);
        tabla.getColumns().add(idCol);
        for (FieldSpec campo : actual.fields()) {
            TableColumn<Object, String> columna = new TableColumn<>(campo.label());
            columna.setCellValueFactory(d -> new ReadOnlyStringWrapper(texto(d.getValue(), campo.name())));
            columna.setPrefWidth(campo.name().equals("descripcion") || campo.name().equals("observaciones") ? 280 : 165);
            tabla.getColumns().add(columna);
        }
        nuevo();
        recargar();
        etiquetaAyuda.setText("Edita un registro seleccionándolo en la tabla. Las relaciones se eligen en las listas desplegables.");
    }

    @FXML private void nuevo() {
        tabla.getSelectionModel().clearSelection();
        editando = null;
        btnEliminar.setDisable(true);
        etiquetaFormulario.setText("Nuevo · " + (actual == null ? "registro" : actual.singular()));
        crearControles();
    }

    private void crearControles() {
        camposFormulario.getChildren().clear();
        controles.clear();
        if (actual == null) return;
        for (FieldSpec campo : actual.fields()) {
            VBox grupo = new VBox(5);
            Label nombre = new Label(campo.label() + (campo.required() ? " *" : ""));
            nombre.getStyleClass().add("field-caption");
            Control control = fabricarControl(campo);
            control.setMaxWidth(Double.MAX_VALUE);
            control.getStyleClass().add("input-app");
            grupo.getChildren().addAll(nombre, control);
            camposFormulario.getChildren().add(grupo);
            controles.put(campo.name(), control);
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private Control fabricarControl(FieldSpec campo) {
        Class<?> tipo = tipoCampo(campo.name());
        if (tipo == Boolean.class) {
            CheckBox c = new CheckBox("Sí / Activo");
            c.setSelected(true);
            return c;
        }
        if (tipo == LocalDate.class) {
            DatePicker c = new DatePicker();
            if (campo.name().equals("fecha") || campo.name().equals("fechaEntrega")) c.setValue(LocalDate.now());
            return c;
        }
        if (tipo.isEnum()) {
            ComboBox c = new ComboBox(FXCollections.observableArrayList(tipo.getEnumConstants()));
            c.setPromptText("Selecciona un estado");
            return c;
        }
        if (!tipo.getName().startsWith("java.")) {
            List<Object> opciones = new ArrayList<>(listado(tipo));
            ComboBox<Object> c = new ComboBox<>(FXCollections.observableArrayList(opciones));
            c.setPromptText("Selecciona " + campo.label().toLowerCase());
            if (tipo == Empresa.class) {
                opciones.sort(Comparator.comparing(Object::toString, String.CASE_INSENSITIVE_ORDER));
                c.setItems(FXCollections.observableArrayList(opciones));
                c.setPromptText("Selecciona una empresa");
                c.setVisibleRowCount(8);
                c.getStyleClass().add("company-selector");
                c.setCellFactory(lista -> crearCeldaEmpresa());
                c.setButtonCell(crearCeldaEmpresa());
            }
            return c;
        }
        if (campo.name().equals("observaciones") || campo.name().equals("descripcion") || campo.name().equals("actividad")) {
            TextArea c = new TextArea();
            c.setPromptText("Escribe " + campo.label().toLowerCase());
            c.setPrefRowCount(3);
            c.setWrapText(true);
            return c;
        }
        TextField t = new TextField();
        t.setPromptText("Escribe " + campo.label().toLowerCase());
        return t;
    }

    private void cargarSeleccion(Object entidad) {
        editando = entidad;
        etiquetaFormulario.setText("Editar · " + actual.singular() + " #" + texto(entidad, "id"));
        btnEliminar.setDisable(false);
        // Reconstruir listas para que las referencias sigan actuales.
        crearControles();
        for (FieldSpec campo : actual.fields()) {
            Control control = controles.get(campo.name());
            Object valor = valor(entidad, campo.name());
            if (control instanceof DatePicker picker) picker.setValue((LocalDate) valor);
            else if (control instanceof CheckBox check) check.setSelected(Boolean.TRUE.equals(valor));
            else if (control instanceof ComboBox<?> combo) {
                // Los objetos relacionados se cargaron en otra sesión JPA; se comparan por ID.
                Object elegido = valor;
                if (valor != null && !valor.getClass().isEnum()) {
                    Long id = (Long) valor(valor, "id");
                    elegido = combo.getItems().stream()
                        .filter(item -> Objects.equals(valor(item, "id"), id)).findFirst().orElse(null);
                }
                ((ComboBox<Object>) combo).setValue(elegido);
            } else if (control instanceof TextInputControl input) input.setText(valor == null ? "" : valor.toString());
        }
    }

    @FXML private void guardar() {
        if (actual == null) return;
        try {
            Object entidad = editando == null ? actual.type().getDeclaredConstructor().newInstance() : editando;
            for (FieldSpec campo : actual.fields()) {
                Control control = controles.get(campo.name());
                Object dato = leerControl(campo, control);
                if (campo.required() && (dato == null || (dato instanceof String s && s.isBlank()))) {
                    throw new IllegalArgumentException("El campo '" + campo.label() + "' es obligatorio.");
                }
                Field campoJava = campo(entidad.getClass(), campo.name());
                campoJava.set(entidad, dato);
            }
            guardarGenerico(entidad);
            recargar();
            nuevo();
            Alertas.informacion("Registro guardado correctamente con Hibernate.");
        } catch (Exception ex) {
            Alertas.error("No se guardó el registro.\n" + ex.getMessage());
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void guardarGenerico(Object obj) { new JpaRepository(actual.type()).save(obj); }

    @FXML private void eliminar() {
        if (editando == null || !Alertas.confirmar("¿Eliminar " + actual.singular().toLowerCase() + " #" + texto(editando, "id") + "?")) return;
        try {
            eliminarGenerico((Long) valor(editando, "id"));
            recargar();
            nuevo();
            Alertas.informacion("Registro eliminado.");
        } catch (Exception ex) { Alertas.error(ex.getMessage()); }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void eliminarGenerico(Long id) { new JpaRepository(actual.type()).delete(id); }

    @FXML private void recargar() {
        if (actual == null) return;
        try {
            todos = new ArrayList<>(listado(actual.type()));
            aplicarFiltro();
        } catch (Exception ex) { etiquetaCantidad.setText("Error de conexión"); Alertas.error(ex.getMessage()); }
    }

    private void aplicarFiltro() {
        String textoFiltro = filtro.getText() == null ? "" : filtro.getText().strip().toLowerCase();
        List<Object> filtrados = todos.stream().filter(item ->
            textoFiltro.isBlank() || actual.fields().stream().anyMatch(f -> texto(item, f.name()).toLowerCase().contains(textoFiltro))
                || texto(item, "id").contains(textoFiltro)).toList();
        tabla.setItems(FXCollections.observableArrayList(filtrados));
        etiquetaCantidad.setText(filtrados.size() + " registro(s) de " + todos.size());
    }

    private Object leerControl(FieldSpec campo, Control control) {
        Class<?> tipo = tipoCampo(campo.name());
        if (control instanceof CheckBox check) return check.isSelected();
        if (control instanceof DatePicker picker) return picker.getValue();
        if (control instanceof ComboBox<?> combo) return combo.getValue();
        if (control instanceof TextInputControl input) {
            String s = input.getText() == null ? "" : input.getText().trim();
            if (s.isEmpty()) return null;
            try {
                if (tipo == Integer.class) return Integer.parseInt(s);
                if (tipo == BigDecimal.class) return new BigDecimal(s.replace(",", "."));
                return s;
            } catch (NumberFormatException ex) {
                throw new IllegalArgumentException("'" + campo.label() + "' requiere un número válido.");
            }
        }
        throw new IllegalArgumentException("Tipo de control desconocido: " + campo.label());
    }

    private static ListCell<Object> crearCeldaEmpresa() {
        return new ListCell<>() {
            {
                getStyleClass().add("company-option");
            }
            @Override protected void updateItem(Object item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.toString());
            }
        };
    }

    private Class<?> tipoCampo(String nombre) { return campo(actual.type(), nombre).getType(); }

    private static Field campo(Class<?> tipo, String nombre) {
        try {
            Field campo = tipo.getDeclaredField(nombre);
            campo.setAccessible(true);
            return campo;
        } catch (NoSuchFieldException ex) {
            throw new IllegalArgumentException("Campo inválido: " + tipo.getSimpleName() + "." + nombre, ex);
        }
    }
    private static Object valor(Object entidad, String nombre) {
        if (entidad == null) return null;
        try { return campo(entidad.getClass(), nombre).get(entidad); }
        catch (IllegalAccessException ex) { throw new IllegalStateException(ex); }
    }
    private static String texto(Object entidad, String nombre) {
        Object resultado = valor(entidad, nombre);
        return resultado == null ? "" : resultado.toString();
    }
    @SuppressWarnings({"rawtypes", "unchecked"})
    private static List<?> listado(Class<?> tipo) { return new JpaRepository(tipo).findAll(); }
}
