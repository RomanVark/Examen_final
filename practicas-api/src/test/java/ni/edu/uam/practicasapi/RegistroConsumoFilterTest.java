package ni.edu.uam.practicasapi;

import ni.edu.uam.practicasapi.service.RegistroConsumoFilter;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class RegistroConsumoFilterTest {
    @Test
    void normalizaIdentificadoresNumericos() {
        assertEquals("/api/estudiantes/{id}", RegistroConsumoFilter.normalizarRuta("/api/estudiantes/25"));
        assertEquals("/api/estudiantes", RegistroConsumoFilter.normalizarRuta("/api/estudiantes"));
        assertEquals("/api/otros", RegistroConsumoFilter.normalizarRuta("/actuator/health"));
    }
}
