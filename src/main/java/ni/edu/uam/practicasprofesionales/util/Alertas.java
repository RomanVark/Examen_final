package ni.edu.uam.practicasprofesionales.util;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import java.util.Optional;

public final class Alertas {
    private Alertas() { }
    public static void informacion(String texto) { mostrar(Alert.AlertType.INFORMATION, "Información", texto); }
    public static void error(String texto) { mostrar(Alert.AlertType.ERROR, "Operación no completada", texto); }
    private static void mostrar(Alert.AlertType tipo, String titulo, String texto) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(texto);
        alerta.getDialogPane().setMinWidth(450);
        alerta.showAndWait();
    }
    public static boolean confirmar(String texto) {
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION, texto, ButtonType.YES, ButtonType.NO);
        alerta.setTitle("Confirmar eliminación");
        alerta.setHeaderText("Esta acción es irreversible");
        Optional<ButtonType> respuesta = alerta.showAndWait();
        return respuesta.isPresent() && respuesta.get() == ButtonType.YES;
    }
}
