package ni.edu.uam.practicasprofesionales.ui;
import java.util.List;
import ni.edu.uam.practicasprofesionales.model.academico.*;
import ni.edu.uam.practicasprofesionales.model.alianzas.*;
import ni.edu.uam.practicasprofesionales.model.asignaciones.*;
import ni.edu.uam.practicasprofesionales.model.seguimiento.*;
public final class EntityCatalog {
    private EntityCatalog() { }
    public static final List<EntityDefinition<?>> ALL = List.of(
        new EntityDefinition<>(Carrera.class, "Carrera", "Carreras", Grupo.CATALOGOS, List.of(new FieldSpec("codigo", "Código", true),new FieldSpec("nombre", "Nombre", true),new FieldSpec("facultad", "Facultad", true),new FieldSpec("activa", "Activa", true))),
        new EntityDefinition<>(Estudiante.class, "Estudiante", "Estudiantes", Grupo.CATALOGOS, List.of(new FieldSpec("carnet", "Carné", true),new FieldSpec("nombres", "Nombres", true),new FieldSpec("apellidos", "Apellidos", true),new FieldSpec("correo", "Correo", true),new FieldSpec("telefono", "Teléfono", false),new FieldSpec("carrera", "Carrera", true),new FieldSpec("activo", "Activo", true))),
        new EntityDefinition<>(Empresa.class, "Empresa", "Empresas", Grupo.CATALOGOS, List.of(new FieldSpec("ruc", "RUC", true),new FieldSpec("razonSocial", "Razón social", true),new FieldSpec("sector", "Sector", true),new FieldSpec("correo", "Correo", true),new FieldSpec("telefono", "Teléfono", false),new FieldSpec("direccion", "Dirección", false),new FieldSpec("activa", "Activa", true))),
        new EntityDefinition<>(TutorAcademico.class, "Tutor académico", "Tutores académicos", Grupo.CATALOGOS, List.of(new FieldSpec("nombres", "Nombres", true),new FieldSpec("apellidos", "Apellidos", true),new FieldSpec("correo", "Correo", true),new FieldSpec("especialidad", "Especialidad", true),new FieldSpec("activo", "Activo", true))),
        new EntityDefinition<>(SupervisorEmpresa.class, "Supervisor de empresa", "Supervisores", Grupo.CATALOGOS, List.of(new FieldSpec("nombres", "Nombres", true),new FieldSpec("apellidos", "Apellidos", true),new FieldSpec("correo", "Correo", true),new FieldSpec("cargo", "Cargo", true),new FieldSpec("empresa", "Empresa", true),new FieldSpec("activo", "Activo", true))),
        new EntityDefinition<>(Convenio.class, "Convenio", "Convenios", Grupo.CATALOGOS, List.of(new FieldSpec("codigo", "Código", true),new FieldSpec("empresa", "Empresa", true),new FieldSpec("fechaInicio", "Fecha inicio", true),new FieldSpec("fechaFin", "Fecha fin", true),new FieldSpec("estado", "Estado", true))),
        new EntityDefinition<>(OfertaPractica.class, "Oferta de práctica", "Ofertas de práctica", Grupo.OPERACIONES, List.of(new FieldSpec("codigo", "Código", true),new FieldSpec("titulo", "Título", true),new FieldSpec("descripcion", "Descripción", false),new FieldSpec("empresa", "Empresa", true),new FieldSpec("cupos", "Cupos", true),new FieldSpec("fechaInicio", "Fecha inicio", true),new FieldSpec("fechaFin", "Fecha fin", true),new FieldSpec("estado", "Estado", true))),
        new EntityDefinition<>(Postulacion.class, "Postulación", "Postulaciones", Grupo.OPERACIONES, List.of(new FieldSpec("estudiante", "Estudiante", true),new FieldSpec("oferta", "Oferta", true),new FieldSpec("fecha", "Fecha", true),new FieldSpec("estado", "Estado", true),new FieldSpec("observaciones", "Observaciones", false))),
        new EntityDefinition<>(Practica.class, "Práctica profesional", "Prácticas profesionales", Grupo.OPERACIONES, List.of(new FieldSpec("estudiante", "Estudiante", true),new FieldSpec("oferta", "Oferta", true),new FieldSpec("tutorAcademico", "Tutor académico", true),new FieldSpec("supervisorEmpresa", "Supervisor empresa", true),new FieldSpec("fechaInicio", "Fecha inicio", true),new FieldSpec("fechaFin", "Fecha fin", true),new FieldSpec("horasRequeridas", "Horas requeridas", true),new FieldSpec("estado", "Estado", true))),
        new EntityDefinition<>(Bitacora.class, "Bitácora", "Bitácoras", Grupo.OPERACIONES, List.of(new FieldSpec("practica", "Práctica", true),new FieldSpec("fecha", "Fecha", true),new FieldSpec("actividad", "Actividad", true),new FieldSpec("horas", "Horas", true),new FieldSpec("validada", "Validada", true))),
        new EntityDefinition<>(Evaluacion.class, "Evaluación", "Evaluaciones", Grupo.OPERACIONES, List.of(new FieldSpec("practica", "Práctica", true),new FieldSpec("fecha", "Fecha", true),new FieldSpec("evaluador", "Evaluador", true),new FieldSpec("calificacion", "Calificación", true),new FieldSpec("observaciones", "Observaciones", false))),
        new EntityDefinition<>(Documento.class, "Documento", "Documentos", Grupo.OPERACIONES, List.of(new FieldSpec("practica", "Práctica", true),new FieldSpec("tipo", "Tipo", true),new FieldSpec("nombreArchivo", "Nombre archivo", true),new FieldSpec("rutaArchivo", "Ruta archivo", true),new FieldSpec("fechaEntrega", "Fecha entrega", true),new FieldSpec("verificado", "Verificado", true)))
    );
    public static List<EntityDefinition<?>> porGrupo(Grupo grupo) {
        return ALL.stream().filter(item -> item.grupo() == grupo).toList();
    }
}
