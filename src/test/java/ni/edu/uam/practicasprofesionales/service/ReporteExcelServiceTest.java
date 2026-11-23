package ni.edu.uam.practicasprofesionales.service;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class ReporteExcelServiceTest {
    @TempDir Path temporal;
    @Test void creaLibroXlsxConEncabezadosFechaYRegistros() throws Exception {
        Path ruta = temporal.resolve("reporte.xlsx");
        ReporteExcelService.generar("Prácticas universitarias",
            List.of("Estudiante","Empresa","Horas"),
            List.of(List.of("Ana López","Empresa Uno","120"),
                    List.of("Juan Pérez","Empresa Dos","80")),ruta);
        assertTrue(Files.size(ruta)>900);
        try(var in=Files.newInputStream(ruta);var libro=new XSSFWorkbook(in)) {
            var hoja=libro.getSheetAt(0);
            assertEquals("Prácticas universitarias",hoja.getRow(0).getCell(0).getStringCellValue());
            assertTrue(hoja.getRow(1).getCell(0).getStringCellValue().startsWith("Generado:"));
            assertEquals("Estudiante",hoja.getRow(3).getCell(0).getStringCellValue());
            assertEquals("Ana López",hoja.getRow(4).getCell(0).getStringCellValue());
            assertEquals("Empresa Dos",hoja.getRow(5).getCell(1).getStringCellValue());
        }
    }
    @Test void rechazaFilasConNumeroIncorrectoDeColumnas(){
        assertThrows(IllegalArgumentException.class,()->ReporteExcelService.generar(
            "Prueba",List.of("A","B"),List.of(List.of("Solo A")),temporal.resolve("error.xlsx")));
    }
}
