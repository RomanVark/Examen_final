package ni.edu.uam.practicasprofesionales.ui;

import ni.edu.uam.practicasprofesionales.model.alianzas.Convenio;
import ni.edu.uam.practicasprofesionales.model.alianzas.Empresa;
import ni.edu.uam.practicasprofesionales.model.alianzas.SupervisorEmpresa;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EmpresaCrudConfiguracionTest {
    @Test
    void empresaEstaDisponibleEnElCatalogoConLosCamposDelCrud() {
        EntityDefinition<?> empresa = EntityCatalog.ALL.stream()
                .filter(definicion -> definicion.type() == Empresa.class)
                .findFirst()
                .orElseThrow();

        assertEquals("Empresa", empresa.singular());
        assertTrue(empresa.fields().stream().anyMatch(campo -> campo.name().equals("ruc") && campo.required()));
        assertTrue(empresa.fields().stream().anyMatch(campo -> campo.name().equals("razonSocial") && campo.required()));
        assertTrue(empresa.fields().stream().anyMatch(campo -> campo.name().equals("correo") && campo.required()));
    }

    @Test
    void supervisorYConvenioExigenSeleccionarEmpresaEnElCrud() {
        EntityDefinition<?> supervisor = definicion(SupervisorEmpresa.class);
        EntityDefinition<?> convenio = definicion(Convenio.class);

        assertTrue(supervisor.fields().stream().anyMatch(campo -> campo.name().equals("empresa") && campo.required()));
        assertTrue(convenio.fields().stream().anyMatch(campo -> campo.name().equals("empresa") && campo.required()));
    }

    private static EntityDefinition<?> definicion(Class<?> tipo) {
        return EntityCatalog.ALL.stream()
                .filter(definicion -> definicion.type() == tipo)
                .findFirst()
                .orElseThrow();
    }
}
