package ni.edu.uam.practicasprofesionales.controller;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

/** Pantalla de demostración: JavaFX consume la API HTTP sin consultar JPA directamente. */
public class ApiConsumoController {
    @FXML private TextField servidorApi;
    @FXML private TextArea resultado;
    @FXML private Label estado;

    private final HttpClient cliente = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(3)).build();

    @FXML public void initialize() {
        servidorApi.setText("http://127.0.0.1:8080");
        resultado.setText("Arranca primero la API Spring Boot; luego elige una consulta.");
    }

    @FXML private void consultarSalud() { solicitar("/actuator/health"); }
    @FXML private void consultarEstudiantes() { solicitar("/api/estudiantes"); }
    @FXML private void consultarEmpresas() { solicitar("/api/empresas"); }
    @FXML private void consultarPracticas() { solicitar("/api/practicas"); }
    @FXML private void consultarConsumo() { solicitar("/api/reportes/consumo?dias=7"); }

    private void solicitar(String ruta) {
        String base = servidorApi.getText().strip().replaceAll("/+$", "");
        if (!base.matches("https?://[^/]+")) {
            estado.setText("Introduce una URL válida (http://127.0.0.1:8080).");
            return;
        }
        URI uri;
        try {
            uri = URI.create(base + ruta);
            HttpRequest solicitud = HttpRequest.newBuilder(uri)
                .timeout(Duration.ofSeconds(10)).GET().build();
            estado.setText("Consultando " + ruta + " ...");
            cliente.sendAsync(solicitud, HttpResponse.BodyHandlers.ofString())
                .orTimeout(12, TimeUnit.SECONDS)
                .whenComplete((respuesta, error) -> Platform.runLater(() -> {
                    if (error != null) {
                        estado.setText("No se pudo conectar. Comprueba que practicas-api esté iniciado.");
                        resultado.setText(error.getMessage());
                    } else {
                        estado.setText("HTTP " + respuesta.statusCode() + " · " + ruta);
                        resultado.setText(respuesta.body());
                    }
                }));
        } catch (IllegalArgumentException ex) {
            estado.setText("La dirección de la API es inválida.");
        }
    }
}
