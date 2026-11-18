# Prácticas Profesionales FX

## 1. Requisitos

- **Java:** Eclipse Temurin JDK 21.0.12.
- **IDE:** IntelliJ IDEA con soporte para Maven y JavaFX.
- **Maven:** 3.6.3 o superior; se incluye Maven Wrapper.
- **PostgreSQL:** versión 15 o superior.
- **Interfaz:** JavaFX 21.0.6.
- **Persistencia:** Jakarta Persistence 3.2 con Hibernate ORM 7.4.13.Final.
- **Dependencias:** Spring Boot 4.1.2 como plataforma de dependencias y Spring Data JPA 4.1.2, seleccionados desde Spring Initializr; PostgreSQL Driver gestionado por Maven.
- **Opcionales:** pgAdmin y Gluon Scene Builder.

La aplicación continúa siendo de escritorio con JavaFX. En esta fase se incorpora Spring Data JPA como dependencia para obtener Hibernate administrado por Spring Boot; no se agrega servidor web ni API REST.

## 2. Instalación y uso

1. **Clonar el repositorio y seleccionar la rama de trabajo:**

   ```powershell
   git clone https://github.com/RomanVark/Examen_final.git
   cd Examen_final
   git switch AlfredoV2
   ```

2. **Configurar Java 21:** en IntelliJ IDEA abre la carpeta que contiene `pom.xml`. En **File → Project Structure → Project**, selecciona Temurin 21.0.12 como Project SDK y recarga Maven.

3. **Crear la base de datos:** en pgAdmin, conéctate a la base `postgres` y ejecuta `sql/00_crear_base.sql`. Omite este paso si ya existe `practicas_profesionales`.

4. **Crear el esquema y los datos de ejemplo:** conéctate a `practicas_profesionales` y ejecuta los siguientes archivos en orden:

   ```text
   sql/01_esquema.sql
   sql/02_datos_ejemplo.sql
   sql/03_consultas_verificacion.sql
   ```

   El esquema contiene las doce tablas utilizadas por los módulos académico, empresas, asignaciones y seguimiento. Los scripts SQL son la fuente de creación del esquema; Hibernate usa `validate` por defecto y no modifica las tablas.

5. **Configurar la conexión en PowerShell:**

   ```powershell
   $env:DB_URL="jdbc:postgresql://localhost:5432/practicas_profesionales"
   $env:DB_USER="postgres"
   $env:DB_PASSWORD="TU_CONTRASEÑA_LOCAL"
   $env:HIBERNATE_DDL_AUTO="validate"
   ```

   Si ejecutas desde el botón **Run** de IntelliJ, configura las mismas variables en **Run → Edit Configurations → Environment variables**. No guardes contraseñas reales en Git.

   `HIBERNATE_DDL_AUTO` acepta `validate`, `none` o `update`. Mantén `validate` para comprobar el esquema sin alterarlo; utiliza `update` únicamente en una base local de desarrollo.

6. **Compilar y ejecutar las pruebas:**

   ```powershell
   .\mvnw.cmd clean test
   ```

   Las pruebas unitarias se ejecutan sin PostgreSQL. Las pruebas de integración se omiten cuando `DB_PASSWORD` no está configurada; para ejecutarlas, prepara primero la base de datos y sus variables de entorno.

7. **Iniciar la aplicación JavaFX:**

   ```powershell
   .\mvnw.cmd javafx:run
   ```

   También puedes usar `mvn clean test` y `mvn javafx:run` si Maven está instalado.

8. **Usar los módulos:** desde el menú lateral abre **Inicio / Resumen**, **Módulo académico**, **Catálogos**, **Gestión de prácticas**, **Reportes HQL** o **Seguimiento / Reportes**. Los formularios permiten crear, consultar, editar y eliminar registros según sus relaciones y reglas de negocio.

9. **Editar las pantallas:** abre los archivos FXML de `src/main/resources/ni/edu/uam/practicasprofesionales/` con Scene Builder. Los estilos se encuentran en la carpeta `styles`.

## 3. Persistencia y Hibernate

- `pom.xml` utiliza el parent de Spring Boot 4.1.2 y la dependencia `spring-boot-starter-data-jpa`, siguiendo la selección de Spring Initializr.
- Spring Boot 4.1.2 administra Hibernate ORM 7.4.13.Final y Jakarta Persistence 3.2; no se fija manualmente otra versión de Hibernate.
- Las entidades se registran explícitamente en `src/main/resources/META-INF/persistence.xml`.
- `JpaUtil` mantiene una única `EntityManagerFactory` y crea un `EntityManager` por operación.
- `JpaRepository<T>` concentra las operaciones CRUD y sus transacciones.
- Los servicios conservan consultas HQL/JPQL para búsquedas, relaciones y reportes.
- Las credenciales se obtienen de variables de entorno; no se incluyen contraseñas en el repositorio.

## 4. Verificación antes de entregar

1. Ejecuta `mvnw.cmd clean test`.
2. Comprueba que la base de datos contiene las doce tablas.
3. Inicia JavaFX y verifica los CRUD de cada módulo.
4. Ejecuta los reportes HQL y exporta un CSV.
5. Comprueba que la aplicación inicia con `HIBERNATE_DDL_AUTO=validate`.
