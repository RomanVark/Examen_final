# Módulo de seguimiento — Celeste Carrasco

Este documento describe exclusivamente la contribución de Celeste al módulo de seguimiento.

## Alcance

- **Bitácora:** registro de actividad por práctica, fecha, horas y estado de validación.
- **Evaluación:** calificación y observaciones asociadas a una práctica.
- **Documento:** control de tipo, nombre, ruta, fecha de entrega y verificación.
- **Reglas de negocio:** horas de bitácora entre 1 y 24; calificación entre 0 y 100; campos obligatorios para documentos.
- **Reportes HQL:** horas acumuladas por práctica y promedio de evaluación por estudiante.
- **Exportación:** CSV en UTF-8, con comillas escapadas y encabezados.

## Dependencias de integración

Las tres entidades se relacionan con \`Practica\`, que pertenece al módulo de asignaciones. El reporte de horas utiliza \`Practica.bitacoras\`, y el promedio utiliza \`Practica.estudiante\`. Por tanto, las clases de dominio y el mapeo de \`Practica\` deben integrarse antes de ejecutar las pruebas completas.

El controlador recibe un \`EntityManager\` mediante \`configurar(EntityManager)\`. La aplicación principal debe proporcionarlo al abrir esta vista. No se modificó el arranque general ni los archivos de los otros módulos.

## Ejecución de pruebas

Con las dependencias JPA/JUnit y la entidad \`Practica\` integradas:

\`\`\`bash
mvn -Dtest=ValidacionSeguimientoTest test
\`\`\`

Para crear las tablas de seguimiento, ejecutar \`sql/seguimiento.sql\` después de crear \`practicas\`.

## Pruebas funcionales pendientes de evidencia

- Crear bitácoras con 1 y 24 horas; rechazar 0 y 25.
- Crear evaluaciones con 0 y 100; rechazar valores inferiores o superiores.
- Rechazar documentos sin tipo, nombre, ruta o fecha.
- Consultar horas acumuladas y promedios de evaluación con datos reales.
- Exportar cada reporte a CSV y comprobar encabezados, caracteres especiales y comillas.
