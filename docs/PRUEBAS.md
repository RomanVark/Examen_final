# Plan de pruebas y criterios de aceptación

## Conexión y configuración

- Comprobar Temurin 21.0.12 con `java -version` y Maven con `mvn -version`.
- Ejecutar en pgAdmin los SQL 00/01/02 y las verificaciones 03.
- Comprobar que sin `DB_PASSWORD` la app muestra un mensaje de configuración, sin exponer credenciales.
- Comprobar que las 12 tablas están disponibles y que los registros aparecen en sus módulos.

## CRUD: ejecutar por cada una de las 12 entidades

| ID | Acción | Resultado esperado |
| --- | --- | --- |
| C1 | Insertar registro válido | Se guarda y aparece en tabla/pgAdmin. |
| C2 | Seleccionarlo y actualizar un campo | Se muestra el cambio luego de Actualizar. |
| C3 | Buscar por ID, nombre o campo | El filtro presenta coincidencias. |
| C4 | Dejar campo obligatorio vacío | Se informa el error y no se guarda. |
| C5 | Duplicar campo único (ej. carnet) | PostgreSQL rechaza el guardado y se conserva integridad. |
| C6 | Eliminar registro sin referencias | Se solicita confirmación y se elimina. |
| C7 | Intentar eliminar carrera con estudiantes | Se impide por integridad referencial. |
| C8 | Crear fecha final anterior al inicio | No guarda; aviso descriptivo. |
| C9 | Crear bitácora con 30 horas | No guarda; aviso descriptivo. |
| C10 | Guardar evaluación con calificación 105 | No guarda; aviso descriptivo. |
| C11 | Asignar supervisor de otra empresa a una práctica | Validación de negocio lo impide. |

## Reportes

- Ejecutar los cuatro reportes HQL. Para las prácticas de ejemplo se deben mostrar estudiante/empresa, horas agregadas, conteos de postulaciones y promedios.
- Exportar un reporte CSV, abrirlo en Excel/LibreOffice y verificar encabezados y comillas de escape.
- Crear un registro desde JavaFX, actualizar consulta y confirmar que refleja datos reales en PostgreSQL.

## Colaboración

- Cada integrante hace sus propios commits y PR con descripción y capturas de pruebas.
- Cada PR recibe revisión de otra cuenta del equipo antes de merge.
- No se suben `target/`, `.idea/` ni contraseñas.
- Ejecutar `mvn clean test`, `mvn clean compile` y `mvn javafx:run` después de cada integración.
