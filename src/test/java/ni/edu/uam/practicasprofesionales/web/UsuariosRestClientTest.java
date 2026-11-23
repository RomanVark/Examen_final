package ni.edu.uam.practicasprofesionales.web;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import static org.junit.jupiter.api.Assertions.*;

class UsuariosRestClientTest {
    @Test void convierteListaEnObjetosConCincoCampos() throws Exception {
        var usuarios = UsuariosRestClient.interpretarLista("""
          [{"id":1,"name":"Leanne Graham","username":"Bret",
            "email":"l@example.test","address":{"city":"Gwenborough"}},
           {"id":2,"name":"Ervin Howell","username":"Anton",
            "email":"e@example.test","address":{"city":"Wisokyburgh"}}]""");
        assertEquals(2, usuarios.size());
        assertEquals("Leanne Graham", usuarios.getFirst().nombre());
        assertEquals("Bret", usuarios.getFirst().usuario());
        assertEquals("l@example.test", usuarios.getFirst().correo());
        assertEquals("Gwenborough", usuarios.getFirst().ciudad());
    }
    @Test void validaDetalleYResultadosVacios() throws Exception {
        var u = UsuariosRestClient.interpretarDetalle(
          "{\"id\":3,\"name\":\"Clementine\",\"username\":\"Samantha\",\"email\":\"c@example.test\"}",3);
        assertEquals(3,u.id());
        assertThrows(IOException.class,()->UsuariosRestClient.interpretarDetalle("{}",999));
        assertThrows(IOException.class,()->UsuariosRestClient.interpretarDetalle("[]",3));
        assertTrue(UsuariosRestClient.interpretarLista("[]").isEmpty());
        assertThrows(IOException.class,()->UsuariosRestClient.interpretarLista("{}"));
    }
    @Test void soloPermiteHttpsOHttpLocal() {
        assertThrows(IllegalArgumentException.class,()->new UsuariosRestClient(
            URI.create("http://example.org"),HttpClient.newHttpClient()));
    }
}
