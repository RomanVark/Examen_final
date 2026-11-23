package ni.edu.uam.practicasprofesionales.web;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Cliente HTTP separado de JavaFX y Hibernate.
 * Consume JSONPlaceholder: usuarios simulados, no registros de la universidad.
 */
public final class UsuariosRestClient {
    private static final String BASE_PUBLICA = "https://jsonplaceholder.typicode.com";
    private final URI base;
    private final HttpClient http;

    public UsuariosRestClient() {
        this(URI.create(BASE_PUBLICA), HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(6))
            .followRedirects(HttpClient.Redirect.NORMAL).build());
    }

    public UsuariosRestClient(URI base, HttpClient http) {
        if (base == null || http == null) throw new IllegalArgumentException("Cliente y URL obligatorios.");
        if (!"https".equalsIgnoreCase(base.getScheme())
            && !("http".equalsIgnoreCase(base.getScheme())
                 && ("localhost".equalsIgnoreCase(base.getHost())
                  || "127.0.0.1".equals(base.getHost())))) {
            throw new IllegalArgumentException("La API debe usar HTTPS (o HTTP local para pruebas).");
        }
        this.base = base;
        this.http = http;
    }

    /** GET /users: primera operación exigida. */
    public List<UsuarioApi> listar() throws IOException, InterruptedException {
        return interpretarLista(obtener("/users"));
    }

    /** Procesar JSON independientemente de red. */
    public static List<UsuarioApi> interpretarLista(String json) throws IOException {
        JsonElement elemento = JsonParser.parseString(json);
        if (!elemento.isJsonArray()) throw new IOException("Respuesta JSON inesperada: se esperaba una lista.");
        List<UsuarioApi> resultado = new ArrayList<>();
        for (JsonElement item : elemento.getAsJsonArray()) {
            resultado.add(convertir(item.getAsJsonObject()));
        }
        return resultado;
    }

    /** GET /users/{id}: segunda operación exigida. */
    public UsuarioApi detalle(int id) throws IOException, InterruptedException {
        if (id <= 0) throw new IllegalArgumentException("El identificador debe ser un entero positivo.");
        return interpretarDetalle(obtener("/users/" + id), id);
    }

    public static UsuarioApi interpretarDetalle(String json, int id) throws IOException {
        JsonElement elemento = JsonParser.parseString(json);
        if (!elemento.isJsonObject() || elemento.getAsJsonObject().size() == 0) {
            throw new IOException("No existe el usuario " + id + " (respuesta vacía).");
        }
        return convertir(elemento.getAsJsonObject());
    }

    private String obtener(String ruta) throws IOException, InterruptedException {
        HttpRequest solicitud = HttpRequest.newBuilder(base.resolve(ruta))
            .timeout(Duration.ofSeconds(10))
            .header("Accept", "application/json")
            .GET().build();
        HttpResponse<String> respuesta;
        try {
            respuesta = http.send(solicitud, HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw ex;
        }
        int estado = respuesta.statusCode();
        if (estado == 404) throw new IOException("HTTP 404: recurso no encontrado: " + ruta);
        if (estado < 200 || estado >= 300) throw new IOException(
            "HTTP " + estado + ": el servicio respondió con un error.");
        return respuesta.body();
    }

    private static UsuarioApi convertir(JsonObject json) throws IOException {
        try {
            int id = json.get("id").getAsInt();
            String nombre = valor(json, "name");
            String usuario = valor(json, "username");
            String correo = valor(json, "email");
            JsonObject direccion = json.has("address") && json.get("address").isJsonObject()
                ? json.getAsJsonObject("address") : new JsonObject();
            String ciudad = valor(direccion, "city");
            if (id <= 0 || nombre.isBlank() || correo.isBlank()) {
                throw new IOException("Respuesta JSON incompleta: id, name o email.");
            }
            return new UsuarioApi(id, nombre, usuario, correo, ciudad);
        } catch (IllegalStateException | UnsupportedOperationException | NumberFormatException | NullPointerException ex) {
            throw new IOException("Formato JSON de usuario inválido.", ex);
        }
    }

    private static String valor(JsonObject obj, String clave) {
        return obj.has(clave) && !obj.get(clave).isJsonNull()
            ? obj.get(clave).getAsString() : "";
    }
}
