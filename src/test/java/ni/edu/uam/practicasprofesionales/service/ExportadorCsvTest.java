package ni.edu.uam.practicasprofesionales.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class ExportadorCsvTest {
    @TempDir Path directorioTemporal;

    @Test void exportaUtf8ConBomYEscapaComillas() throws IOException {
        Path archivo = directorioTemporal.resolve("seguimiento.csv");
        List<List<?>> filas = new ArrayList<>();
        filas.add(Arrays.asList("Actividad, con coma", "Dijo \"listo\""));
        filas.add(Arrays.asList("Práctica ñ", null));

        ExportadorCsv.exportar(archivo, List.of("Actividad", "Detalle"), filas);

        String contenido = Files.readString(archivo, StandardCharsets.UTF_8);
        assertTrue(contenido.startsWith("\uFEFF"));
        assertTrue(contenido.contains("\"Actividad, con coma\",\"Dijo \"\"listo\"\"\""));
        assertTrue(contenido.contains("\"Práctica ñ\",\"\""));
    }
}
