# Seguimiento — contribución de Celeste Carrasco

Este módulo registra y consulta el avance de las prácticas profesionales. Se integra con la entidad `Practica` del módulo de asignaciones y utiliza las entidades `Bitacora`, `Evaluacion` y `Documento`.

## Funcionalidad

- **Bitácora:** fecha, actividad, horas y estado de validación.
- **Evaluación:** fecha, evaluador, calificación y observaciones.
- **Documento:** tipo, nombre, ruta, fecha de entrega y verificación.
- **Validaciones:** la práctica asociada debe estar registrada; las horas deben estar entre 1 y 24; la calificación debe estar entre 0 y 100; los campos requeridos no pueden quedar vacíos.
- **Reportes HQL:** horas acumuladas por práctica y promedio de evaluación por estudiante.
- **Exportación CSV:** archivo UTF-8 con BOM para facilitar la apertura en Excel y escape de comillas.

## Integración en la aplicación

Las entidades de seguimiento se administran desde el CRUD general de prácticas. La opción **Seguimiento / Reportes** de la navegación abre la vista de reportes del módulo. El controlador recibe un `EntityManager` para ejecutar las consultas HQL; la sesión se cierra al abandonar la vista o cerrar la aplicación.

Las reglas se centralizan en `ValidacionSeguimiento` y se ejecutan desde los callbacks `@PrePersist` y `@PreUpdate` de las tres entidades, además de las validaciones generales del repositorio.

## Archivos principales

- `model/seguimiento/Bitacora.java`
- `model/seguimiento/Evaluacion.java`
- `model/seguimiento/Documento.java`
- `repository/ValidacionSeguimiento.java`
- `service/ReporteSeguimientoService.java`
- `service/ExportadorCsv.java`
- `controller/SeguimientoReportesController.java`
- `resources/ni/edu/uam/practicasprofesionales/seguimiento-reportes.fxml`

## Pruebas

Ejecutar las pruebas específicas con Maven:

```bash
mvn -Dtest=ValidacionSeguimientoTest,ExportadorCsvTest test
```

Las pruebas cubren límites de horas, límites de calificación, asociación con una práctica registrada, campos obligatorios de documentos y formato de exportación CSV.

Para preparar la base de datos, ejecutar `sql/seguimiento.sql` después de crear la tabla `practicas`, siguiendo el orden de los scripts SQL del proyecto.
