# Módulo de seguimiento — Celeste Carrasco

**Rama de trabajo:** `Celeste`  
**Base de trabajo:** rama `Alfredo` después de completar el módulo de asignaciones.

## Dependencia del trabajo previo

Este módulo depende de la entidad `Practica` y de la estructura compartida de persistencia, validaciones, interfaz y reportes. Por eso el trabajo de seguimiento se desarrolla sobre la base que ya contiene las entidades `OfertaPractica`, `Postulacion` y `Practica`, junto con Hibernate/JPA, el CRUD genérico y PostgreSQL.

No se reemplazaron esas clases ni se creó una segunda estructura del proyecto. Se reutilizaron las entidades `Bitacora`, `Evaluacion` y `Documento`, sus relaciones con `Practica`, el catálogo de entidades y la pantalla general de reportes que ya existen en la base compartida.

## Alcance asignado

- Comprobar la relación `Practica → Bitacora/Evaluacion/Documento`.
- Validar horas de bitácora entre 1 y 24 por día.
- Validar calificaciones entre 0 y 100.
- Exigir una práctica registrada para cada bitácora, evaluación y documento.
- Mantener los campos obligatorios de los documentos.
- Consultar horas registradas y evaluaciones mediante HQL.
- Exportar los resultados a CSV.

## Cambios realizados en esta rama

### Validaciones

Se reforzó `ValidacionDeNegocio` para rechazar bitácoras, evaluaciones y documentos sin una práctica registrada. La bitácora debe registrar entre 1 y 24 horas; la evaluación acepta calificaciones de 0 a 100. Los campos obligatorios de Documento se validan a partir de las anotaciones JPA de la entidad.

### Reportes

Se conservaron las consultas existentes de asignaciones y se agregó el detalle de evaluaciones por práctica. El reporte de horas utiliza la relación `Practica.bitacoras` para sumar las horas registradas. La exportación CSV conserva UTF-8 e incorpora BOM para mejorar la lectura de tildes y eñes en Excel.

### Pruebas

Se ampliaron las pruebas unitarias para cubrir:
- Bitácora con más de 24 horas.
- Bitácora sin práctica registrada.
- Evaluación con calificación máxima de 100.
- Evaluación sin práctica registrada.
- Documento sin práctica registrada.
- Documento con un campo obligatorio ausente.

## Archivos de esta contribución

- Modificado: `src/main/java/ni/edu/uam/practicasprofesionales/repository/ValidacionDeNegocio.java`
- Modificado: `src/main/java/ni/edu/uam/practicasprofesionales/service/ReporteHqlService.java`
- Modificado: `src/main/java/ni/edu/uam/practicasprofesionales/controller/ReportesController.java`
- Modificado: `src/test/java/ni/edu/uam/practicasprofesionales/repository/ValidacionDeNegocioTest.java`
- Añadido: `docs/SEGUIMIENTO_CELESTE.md`

## Comprobación pendiente

La rama contiene el código y las pruebas, pero la ejecución completa debe verificarse en el entorno del equipo con PostgreSQL y Maven:

```bash
mvn clean test
mvn clean compile
mvn javafx:run
```

También debe comprobarse desde JavaFX que se puedan registrar bitácoras, evaluaciones y documentos asociados a una práctica existente, ejecutar los reportes y abrir el CSV exportado.

## Revisión

Según la secuencia de revisión del plan de trabajo, el PR de Celeste debe ser revisado por Roman antes de integrarse a `master`.
