# Prácticas Profesionales FX

## 1. Requisitos

- **Java:** Eclipse Temurin JDK 21.0.12 (o un JDK 21 compatible).
- **IDE:** IntelliJ IDEA con soporte para Maven y JavaFX.
- **Maven:** 3.9 o superior, o Maven Wrapper incluido en el proyecto.
- **PostgreSQL:** versión 15 o superior y permisos para crear una base de datos.
- **Dependencias del proyecto:** JavaFX 21.0.6, Hibernate ORM 6.6.59.Final y controlador PostgreSQL 42.7.14 (gestionadas mediante pom.xml).
- **Opcionales:** pgAdmin para ejecutar SQL y Gluon Scene Builder para editar FXML.
- **Conexión local:** PostgreSQL disponible en el puerto correspondiente (habitualmente 5432) y usuario con contraseña.

## 2. Instalación y uso

1. **Clonar el repositorio y abrir la rama Roman:**

   ~~~powershell
   git clone https://github.com/RomanVark/Examen_final.git
   cd Examen_final
   git switch Roman
   ~~~

2. **Configurar Java 21 e IntelliJ:** abrir la carpeta del proyecto que contiene pom.xml. En **File → Project Structure → Project**, configurar Temurin 21.0.12 como SDK y recargar las dependencias Maven.

3. **Crear la base de datos:** en pgAdmin, conectarse a la base existente **postgres** y ejecutar **sql/00_crear_base.sql**. Omitirlo si ya existe la base **practicas_profesionales**.

4. **Preparar las doce tablas y los datos de prueba:** conectarse en pgAdmin a **practicas_profesionales** y ejecutar exactamente en este orden:

   ~~~text
   sql/01_esquema.sql
   sql/02_datos_ejemplo.sql
   sql/03_consultas_verificacion.sql
   ~~~

   La aplicación valida las doce tablas al iniciar. No basta con ejecutar sql/01_academico.sql, que solo crea tres. Si una tabla ya existe con estructura diferente, el comando CREATE TABLE IF NOT EXISTS no la actualiza automáticamente.

5. **Configurar las credenciales:** en la terminal PowerShell de IntelliJ, desde la carpeta raíz, establecer las variables de entorno con los valores de tu instalación:

   ~~~powershell
   $env:DB_URL="jdbc:postgresql://localhost:5432/practicas_profesionales"
   $env:DB_USER="postgres"
   $env:DB_PASSWORD="TU_CONTRASEÑA_LOCAL"
   ~~~

   Si ejecutas mediante el botón **Run** de IntelliJ, configura las mismas variables en **Run → Edit Configurations → Environment variables**, porque puede no heredarlas de la terminal. No guardar contraseñas reales en Git.

6. **Compilar y ejecutar la aplicación:**

   ~~~powershell
   .\mvnw.cmd clean compile
   .\mvnw.cmd javafx:run
   ~~~

   Si Maven está instalado también puedes utilizar los comandos mvn clean compile y mvn javafx:run.

7. **Navegar por el sistema:** desde el menú lateral, abre **Inicio / Resumen**, **Módulo académico** (carreras, estudiantes, tutores), **Catálogos** (empresas, supervisores, convenios), **Gestión de prácticas** (ofertas, postulaciones, prácticas, bitácoras, evaluaciones y documentos), **Reportes HQL** o **Seguimiento / Reportes**. Para agregar o editar un registro completa los campos y guarda; para eliminar selecciona el registro y confirma. Los reportes permiten consultar resultados relacionados y exportarlos a CSV.

8. **Ejecutar las pruebas unitarias (opcional):**

   ~~~powershell
   .\mvnw.cmd test
   ~~~

   Las pruebas que interactúan con PostgreSQL requieren la base y las variables de conexión configuradas. Si aparece **Unable to build Hibernate SessionFactory**, revisa la causa detallada **Caused by:** en la consola de IntelliJ, las credenciales, el nombre de la base y el esquema de las doce tablas.

9. **Editar las pantallas (opcional):** abrir los archivos FXML de **src/main/resources/ni/edu/uam/practicasprofesionales/** con Scene Builder. El diseño CSS se encuentra en la carpeta **styles**. Los campos del CRUD genérico se generan dinámicamente desde Java.
