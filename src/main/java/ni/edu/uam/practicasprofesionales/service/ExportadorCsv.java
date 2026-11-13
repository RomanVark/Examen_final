package ni.edu.uam.practicasprofesionales.service;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

/** Exportación CSV UTF-8 con escape de comillas y separadores. */
public final class ExportadorCsv {
    private ExportadorCsv() { }

    public static void exportar(Path destino, List<String> encabezados,
                                List<? extends List<?>> filas) throws IOException {
        if (destino == null) throw new IllegalArgumentException("Seleccione dónde guardar el archivo.");
        if (encabezados == null || filas == null)
            throw new IllegalArgumentException("No hay datos para exportar.");
        try (BufferedWriter writer = Files.newBufferedWriter(destino, StandardCharsets.UTF_8)) {
            writer.write(convertirFila(encabezados));
            writer.newLine();
            for (List<?> fila : filas) {
                writer.write(convertirFila(fila));
                writer.newLine();
            }
        }
    }

    private static String convertirFila(List<?> fila) {
        return fila.stream()
            .map(valor -> "\"" + (valor == null ? "" : valor.toString()).replace("\"", "\"\"") + "\"")
            .collect(Collectors.joining(","));
    }
}
