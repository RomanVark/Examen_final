package ni.edu.uam.practicasprofesionales.model.alianzas;

import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class EmpresaRelacionesTest {
    @Test
    void supervisorDebeReferenciarUnaEmpresaConClaveForaneaRestrictiva() throws Exception {
        Field empresa = SupervisorEmpresa.class.getDeclaredField("empresa");
        ManyToOne relacion = empresa.getAnnotation(ManyToOne.class);
        JoinColumn columna = empresa.getAnnotation(JoinColumn.class);

        assertNotNull(relacion);
        assertFalse(relacion.optional());
        assertNotNull(columna);
        assertEquals("empresa_id", columna.name());
        assertFalse(columna.nullable());
        assertEquals("fk_supervisores_empresa_empresa", columna.foreignKey().name());
    }

    @Test
    void convenioDebeReferenciarUnaEmpresaConClaveForaneaRestrictiva() throws Exception {
        Field empresa = Convenio.class.getDeclaredField("empresa");
        ManyToOne relacion = empresa.getAnnotation(ManyToOne.class);
        JoinColumn columna = empresa.getAnnotation(JoinColumn.class);

        assertNotNull(relacion);
        assertFalse(relacion.optional());
        assertNotNull(columna);
        assertEquals("empresa_id", columna.name());
        assertFalse(columna.nullable());
        assertEquals("fk_convenios_empresa_empresa", columna.foreignKey().name());
    }

    @Test
    void empresaMantieneLasRelacionesSinBorradoEnCascada() throws Exception {
        Field supervisores = Empresa.class.getDeclaredField("supervisores");
        Field convenios = Empresa.class.getDeclaredField("convenios");
        OneToMany relacionSupervisores = supervisores.getAnnotation(OneToMany.class);
        OneToMany relacionConvenios = convenios.getAnnotation(OneToMany.class);

        assertNotNull(relacionSupervisores);
        assertNotNull(relacionConvenios);
        assertEquals("empresa", relacionSupervisores.mappedBy());
        assertEquals("empresa", relacionConvenios.mappedBy());
        assertEquals(0, relacionSupervisores.cascade().length);
        assertEquals(0, relacionConvenios.cascade().length);
    }
}
