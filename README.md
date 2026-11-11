# PracticasProfesionalesFX — módulo académico

Aplicación de escritorio JavaFX 21 para la gestión académica de prácticas profesionales. Proyecto modular Maven con Hibernate ORM, JPA y PostgreSQL. **Esta rama solo contiene la contribución de Roman**: carreras, estudiantes, tutores, dashboard, SQL y pruebas. La integración final debe sumar otras nueve entidades desde ramas de los demás integrantes.

## Requisitos
- Eclipse Temurin **JDK 21.0.12** (Project SDK y Maven Runner en IntelliJ IDEA).
- Maven 3.9 o Maven Wrapper ya presente en la plantilla.
- PostgreSQL y pgAdmin / psql.
- JavaFX FXML compatible con Scene Builder: `src/main/resources/ni/edu/uam/practicasprofesionales/views/`.

## Crear y poblar la base de datos
1. Conectar en pgAdmin a la base `postgres` y ejecutar `sql/00_crear_base.sql`. Si ya existe la base, **no volver a crearla**.
2. Cambiar la conexión a `practicas_profesionales`.
3. Ejecutar `sql/01_academico.sql` y `sql/02_ejemplos_academicos.sql`, en ese orden.
4. Ejecutar `sql/03_verificaciones_academicas.sql`.

El script académico crea **3 tablas**, que son la primera cuarta parte de las **12 tablas** definidas para el trabajo completo. Sus nombres y columnas coinciden con la estructura general ya planificada.

## Ejecutar desde PowerShell (sin credenciales almacenadas en Git)
```powershell
$env:DB_URL="jdbc:postgresql://localhost:5432/practicas_profesionales"
$env:DB_USER="postgres"
$env:DB_PASSWORD="1234"   # contraseña de práctica solicitada; no subir credenciales
./mvnw.cmd clean javafx:run
```
Si Maven está instalado, también puede usarse `mvn clean javafx:run`.
Cambiar valores según el PostgreSQL de cada equipo; `.env.example` es un ejemplo, **no se carga automáticamente**.

## Comprobaciones
```powershell
./mvnw.cmd clean test
```
Se incluyen 5 pruebas unitarias independientes de la base y 2 pruebas de integración CRUD que necesitan `DB_PASSWORD` y tablas SQL preparadas. Las pruebas de integración se omiten si falta la variable de entorno.

## Flujo Git
Usar la rama existente `Roman`. No fusionarla sin revisión de Cristopher y comprobación de compilación, ejecución y registros SQL. Evidencias y checklist: [docs/ACADEMICO_ROMAN.md](docs/ACADEMICO_ROMAN.md).

**No hay capturas reales adjuntas ni se declara haber ejecutado Maven o PostgreSQL en este entorno.**

