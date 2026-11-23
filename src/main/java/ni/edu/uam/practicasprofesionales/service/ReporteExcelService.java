package ni.edu.uam.practicasprofesionales.service;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/** Genera un Excel XLSX real con encabezados, filas, fecha y filtros de columnas. */
public final class ReporteExcelService {
    private ReporteExcelService() { }

    public static void generar(String titulo, List<String> columnas,
                               List<List<String>> filas, Path archivo) throws IOException {
        if (titulo == null || columnas == null || columnas.isEmpty() || filas == null) {
            throw new IllegalArgumentException("Título, columnas y filas son obligatorios.");
        }
        try (XSSFWorkbook libro = new XSSFWorkbook()) {
            var hoja = libro.createSheet("Reporte");
            Row tituloFila = hoja.createRow(0);
            tituloFila.createCell(0).setCellValue(titulo);
            Row fecha = hoja.createRow(1);
            fecha.createCell(0).setCellValue("Generado: "
                + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            Font negrita = libro.createFont();
            negrita.setBold(true);
            negrita.setColor(IndexedColors.WHITE.getIndex());
            CellStyle encabezado = libro.createCellStyle();
            encabezado.setFont(negrita);
            encabezado.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            encabezado.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            Row filaEncabezado = hoja.createRow(3);
            for (int i = 0; i < columnas.size(); i++) {
                Cell celda = filaEncabezado.createCell(i);
                celda.setCellValue(columnas.get(i));
                celda.setCellStyle(encabezado);
                hoja.setColumnWidth(i, 6200);
            }
            for (int n = 0; n < filas.size(); n++) {
                List<String> valores = filas.get(n);
                if (valores.size() != columnas.size()) {
                    throw new IllegalArgumentException("Cantidad de columnas inconsistente en fila " + n);
                }
                Row fila = hoja.createRow(n + 4);
                for (int j = 0; j < valores.size(); j++) {
                    fila.createCell(j).setCellValue(valores.get(j) == null ? "" : valores.get(j));
                }
            }
            hoja.createFreezePane(0, 4);
            hoja.setAutoFilter(new org.apache.poi.ss.util.CellRangeAddress(
                3, Math.max(3, filas.size() + 3), 0, columnas.size() - 1));
            try (OutputStream salida = Files.newOutputStream(archivo)) {
                libro.write(salida);
            }
        }
    }
}
