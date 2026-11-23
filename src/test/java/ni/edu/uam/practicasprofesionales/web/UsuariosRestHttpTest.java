package ni.edu.uam.practicasprofesionales.web;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas HTTP reales contra un servidor local efímero: listado, detalle,
 * respuesta 404 y error 500 sin depender de una API pública.
 */
class UsuariosRestHttpTest {
    private HttpServer servidor;
    private UsuariosRestClient cliente;

    @BeforeEach void preparar() throws Exception {
        servidor = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        servidor.createContext("/users", intercambio -> {
            String ruta = intercambio.getRequestURI().getPath();
            int codigo = ruta.equals("/users/500") ? 500
                : ruta.equals("/users/404") ? 404 : 200;
            String json = switch (ruta) {
                case "/users" -> "[{\"id\":1,\"name\":\"Ana\",\"username\":\"ana\","
                    + "\"email\":\"ana@example.test\",\"address\":{\"city\":\"Managua\"}}]";
                case "/users/1" -> "{\"id\":1,\"name\":\"Ana\",\"username\":\"ana\","
                    + "\"email\":\"ana@example.test\",\"address\":{\"city\":\"Managua\"}}";
                default -> "{}";
            };
            byte[] cuerpo = json.getBytes(StandardCharsets.UTF_8);
            intercambio.getResponseHeaders().add("Content-Type", "application/json");
            intercambio.sendResponseHeaders(codigo, cuerpo.length);
            try (var out = intercambio.getResponseBody()) { out.write(cuerpo); }
        });
        servidor.start();
        cliente = new UsuariosRestClient(
            URI.create("http://127.0.0.1:" + servidor.getAddress().getPort()),
            HttpClient.newHttpClient());
    }

    @AfterEach void cerrar() {
        if (servidor != null) servidor.stop(0);
    }

    @Test void listaYDetalleConsumenDosEndpointsHttp() throws Exception {
        var lista = cliente.listar();
        assertEquals(1, lista.size());
        assertEquals("Managua", lista.getFirst().ciudad());
        var detalle = cliente.detalle(1);
        assertEquals("ana@example.test", detalle.correo());
    }

    @Test void controlaHttp404Y500() {
        IOException noExiste = assertThrows(IOException.class, () -> cliente.detalle(404));
        assertTrue(noExiste.getMessage().contains("404"));
        IOException falla = assertThrows(IOException.class, () -> cliente.detalle(500));
        assertTrue(falla.getMessage().contains("500"));
    }

    @Test void validaIdAntesDeEnviarSolicitud() {
        assertThrows(IllegalArgumentException.class, () -> cliente.detalle(-1));
        assertThrows(IllegalArgumentException.class, () -> cliente.detalle(0));
    }
}
