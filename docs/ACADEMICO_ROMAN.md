# Entrega del módulo académico — Roman

**Rama real utilizada:** `Roman`. El plan HTML propone `feat/academico-roman`, pero ya existía `Roman` en el repositorio.
**Responsabilidad:** 25 % del proyecto, exclusivamente `Carrera`, `Estudiante` y `TutorAcademico`.

## Alcance programado
- Entidades JPA y relación `Carrera (1) → (N) Estudiante`.
- Operaciones de crear, listar, actualizar y eliminar en Hibernate/JPA con transacciones.
- Reglas: carné y correo únicos, códigos únicos, campos obligatorios, correos válidos y estados.
- JavaFX: tres pestañas de gestión, formularios FXML Scene Builder y estilo CSS.
- Dashboard: conteos y estudiantes por carrera; consultas HQL con `JOIN` y `LEFT JOIN`.
- Pruebas unitarias sin BD; dos pruebas de integración CRUD opcionales con BD.

## Lista de capturas de evidencia (pendientes de obtener en IntelliJ)
1. `ROMAN_01_Conexion_PostgreSQL.png` — resultado de sql/03_verificaciones_academicas.sql.
2. `ROMAN_02_Crear_Carrera.png` — alta de una carrera y tabla actualizada.
3. `ROMAN_03_Crear_Editar_Estudiante.png` — alumno con carrera asociada.
4. `ROMAN_04_Tutores_CRUD.png` — creación y edición de tutor.
5. `ROMAN_05_Error_Correo_Duplicado.png` — validación del correo único.
6. `ROMAN_06_Dashboard.png` — indicadores con datos de PostgreSQL.
7. `ROMAN_07_Pruebas_Maven.png` — `mvn test` completado.
8. `ROMAN_08_Historial_Commits_Roman.png` — evidencia de cambios reales.

> Las capturas no están adjuntas ni se afirma que se hayan ejecutado pruebas locales.
> Crear estas evidencias únicamente después de comprobar el funcionamiento real.

## Comandos de verificación
```powershell
$env:DB_URL="jdbc:postgresql://localhost:5432/practicas_profesionales"
$env:DB_USER="postgres"
$env:DB_PASSWORD="1234"   # Solo si es una credencial de laboratorio local
mvn clean test
mvn javafx:run
```

## Revisión de PR
- Revisor propuesto: Cristopher Santana.
- Confirmar que `master` tenga una versión base acordada antes de fusionar.
- No añadir módulos de empresas, ofertas, prácticas ni evaluaciones en esta rama.

