package ni.edu.uam.practicasprofesionales.ui;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas de presentación sin PostgreSQL: contratos FXML y construcción
 * de componentes bajo un servidor gráfico virtual.
 */
class InterfazFxmlTest {
    private static final String BASE = "/ni/edu/uam/practicasprofesionales/";
    private static final List<String> VISTAS = List.of(
        "views/principal.fxml",
        "views/inicio.fxml",
        "views/academico.fxml",
        "views/crud.fxml",
        "views/reportes.fxml",
        "seguimiento-reportes.fxml"
    );
    private static final Pattern CONTROLLER = Pattern.compile("fx:controller=\"([^\"]+)\"");
    private static final Pattern ID = Pattern.compile("fx:id=\"([^\"]+)\"");
    private static final Pattern ACTION = Pattern.compile("onAction=\"#([^\"]+)\"");

    private String leer(String ruta) throws IOException {
        try (var stream = getClass().getResourceAsStream(BASE + ruta)) {
            assertNotNull(stream, "No existe el FXML: " + ruta);
            return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    @Test
    void todosLosFxmlTienenControlesYHandlersExistentes() throws Exception {
        for (String ruta : VISTAS) {
            String xml = leer(ruta);
            Matcher matcher = CONTROLLER.matcher(xml);
            assertTrue(matcher.find(), "Sin fx:controller: " + ruta);
            Class<?> controlador = Class.forName(matcher.group(1));
            Set<String> campos = Arrays.stream(controlador.getDeclaredFields())
                .map(Field::getName).collect(Collectors.toSet());
            Set<String> acciones = Arrays.stream(controlador.getDeclaredMethods())
                .map(Method::getName).collect(Collectors.toSet());

            Matcher ids = ID.matcher(xml);
            while (ids.find()) {
                assertTrue(campos.contains(ids.group(1)), ruta + " tiene fx:id sin campo: " + ids.group(1));
            }
            Matcher eventos = ACTION.matcher(xml);
            while (eventos.find()) {
                assertTrue(acciones.contains(eventos.group(1)),
                    ruta + " tiene acción no implementada: " + eventos.group(1));
            }
            assertFalse(Pattern.compile("<TableView[^>]+\\bplaceholder=\"").matcher(xml).find(),
                ruta + " usa placeholder como String en vez de Node");
        }
    }

    @Test
    void todasLasVistasSeConstruyenConJavaFxSinAccederALaBase() throws Exception {
        Assumptions.assumeTrue(
            "true".equalsIgnoreCase(System.getenv("RUN_FXML_UI_SMOKE")),
            "Ejecutar bajo Xvfb con RUN_FXML_UI_SMOKE=true."
        );
        CountDownLatch inicio = new CountDownLatch(1);
        Platform.startup(inicio::countDown);
        assertTrue(inicio.await(20, TimeUnit.SECONDS), "JavaFX no logró iniciar");
        try {
            for (String ruta : VISTAS) {
                // Desactivar solo controladores/eventos para validar la estructura visual.
                // Los contratos de fx:id y onAction se revisan en la prueba anterior.
                String visual = leer(ruta)
                    .replaceAll("\\sfx:controller=\"[^\"]+\"", "")
                    .replaceAll("\\sonAction=\"#[^\"]+\"", "");
                CountDownLatch terminado = new CountDownLatch(1);
                AtomicReference<Throwable> fallo = new AtomicReference<>();
                Platform.runLater(() -> {
                    try {
                        FXMLLoader loader = new FXMLLoader();
                        Node nodo = loader.load(
                            new ByteArrayInputStream(visual.getBytes(StandardCharsets.UTF_8)));
                        assertNotNull(nodo);
                    } catch (Throwable error) {
                        fallo.set(error);
                    } finally {
                        terminado.countDown();
                    }
                });
                assertTrue(terminado.await(30, TimeUnit.SECONDS),
                    "Tiempo agotado cargando " + ruta);
                if (fallo.get() != null) {
                    fail("No se pudo construir la vista " + ruta + ": " + fallo.get(), fallo.get());
                }
            }
        } finally {
            Platform.exit();
        }
    }
}
