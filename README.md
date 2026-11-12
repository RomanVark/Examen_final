# PracticasProfesionalesFX

**Proyecto final de Programación de Aplicaciones de Escritorio**. Sistema JavaFX + Hibernate ORM + PostgreSQL para administrar prácticas profesionales universitarias.

> **Repositorio original**: [RomanVark/Examen_final](https://github.com/RomanVark/Examen_final). Se revisó únicamente como referencia de estructura Maven/JavaFX. Esta entrega es independiente; no se escribió ningún archivo en ese repositorio.

## Requisitos del equipo

- **Eclipse Temurin JDK 21.0.12** (configurarlo como Project SDK en IntelliJ IDEA; también `JAVA_HOME`). El proyecto compila con `--release 21`.
- Maven 3.9.x o superior; IntelliJ IDEA compatible con Maven.
- PostgreSQL 15 o superior (recomendado), usuario local **postgres**.
- JavaFX **21.0.6**, Hibernate ORM **6.6.59.Final**, pgJDBC **42.7.14** (se descargan con Maven al abrir el proyecto).
- Opcional: **Gluon Scene Builder** para editar `.fxml` visualmente.

## Instalación en Windows (paso a paso)

1. Instala Temurin **21.0.12** y comprueba `java -version` (la implementación Temurin instalada en el equipo debe mostrar 21.0.12). En IntelliJ: **File → Project Structure → Project SDK → Add JDK**, seleccionar esa instalación.
2. Instala PostgreSQL y anota el puerto, normalmente **5432**. Para el entorno de ejemplo se asumirá **usuario `postgres`** y **contraseña local `1234`**. Si tu instalación tiene otra contraseña, no la cambies por obligación; ajusta tu variable de entorno.
3. Abre pgAdmin. En la base existente `postgres` ejecuta `sql/00_crear_base.sql`. A continuación selecciona la nueva base `practicas_profesionales` y ejecuta, en este orden, `sql/01_esquema.sql` y `sql/02_datos_ejemplo.sql`.
4. Comprueba las 12 tablas y los 64 registros de ejemplo con `sql/03_consultas_verificacion.sql`.
5. Abre **esta carpeta completa**, que contiene `pom.xml`, en IntelliJ IDEA, importa el proyecto Maven y usa JDK Temurin 21.0.12.
6. Antes de ejecutar, abre **PowerShell** en esa terminal de IntelliJ y establece las variables de la sesión:

   ```powershell
   $env:DB_URL="jdbc:postgresql://localhost:5432/practicas_profesionales"
   $env:DB_USER="postgres"
   $env:DB_PASSWORD="1234"  # SOLO contraseña de ejemplo local; no subir archivos con secretos
   mvn clean javafx:run
   ```

   En **CMD** (no PowerShell):

   ```cmd
   set DB_URL=jdbc:postgresql://localhost:5432/practicas_profesionales
   set DB_USER=postgres
   set DB_PASSWORD=1234
   mvn clean javafx:run
   ```

   En Linux/macOS: `export DB_URL=jdbc:postgresql://localhost:5432/practicas_profesionales`, `export DB_USER=postgres`, `export DB_PASSWORD=1234`, luego `mvn clean javafx:run`.

7. Si IntelliJ muestra `DB_PASSWORD` ausente, define las variables **dentro de la Run Configuration** de IntelliJ, ya que sus configuraciones de ejecución pueden no heredar las variables de la terminal. Nunca guardes contraseñas en el repositorio.
8. Prueba el flujo: **Catálogos → Carreras → Estudiantes → Empresas**, luego **Gestión de prácticas → Ofertas → Postulaciones → Prácticas → Bitácoras/Evaluaciones**, y **Reportes HQL → Exportar CSV**.

### Crear la base usando psql (alternativa a pgAdmin)

```powershell
psql -h localhost -U postgres -W -d postgres -f sql/00_crear_base.sql
psql -h localhost -U postgres -W -d practicas_profesionales -f sql/01_esquema.sql
psql -h localhost -U postgres -W -d practicas_profesionales -f sql/02_datos_ejemplo.sql
psql -h localhost -U postgres -W -d practicas_profesionales -f sql/03_consultas_verificacion.sql
```

`-W` solicita interactivamente la contraseña. Para el ejemplo local: `1234`. Si ya existe la base, omite `00_crear_base.sql`. No ejecutes los scripts sobre una base con datos de producción.

## Estructura

```text
PracticasProfesionalesFX/
├── pom.xml
├── README.md
├── .gitignore / .env.example
├── sql/
│   ├── 00_crear_base.sql
│   ├── 01_esquema.sql
│   ├── 02_datos_ejemplo.sql
│   └── 03_consultas_verificacion.sql
├── docs/
│   ├── ARQUITECTURA.md
│   ├── PRUEBAS.md
│   ├── GUIA_SCENEBUILDER.md
│   ├── REPARTO_TRABAJO_4.html
│   └── EVIDENCIAS.md
└── src/
    ├── main/java/module-info.java
    ├── main/java/ni/edu/uam/practicasprofesionales/
    │   ├── app/           # aplicación principal y launcher
    │   ├── controller/    # navegación, CRUD genérico y reportes
    │   ├── model/         # 12 @Entity + model/enums/
    │   ├── persistence/   # EntityManagerFactory (config env)
    │   ├── repository/    # CRUD con JPA + validaciones
    │   ├── service/       # 4 consultas HQL con JOIN
    │   ├── ui/            # catálogo de módulos y campos
    │   └── util/          # alertas e interfaz
    ├── main/resources/META-INF/persistence.xml
    ├── main/resources/ni/edu/uam/practicasprofesionales/
    │   ├── views/         # cuatro FXML modificables visualmente
    │   └── styles/        # app.css
    └── test/java/        # pruebas JUnit 5
```

## Funcionalidad implementada

Las **12 entidades** con `@Entity`, `@Table` y `@Id` están organizadas en los paquetes `model.academico`, `model.alianzas`, `model.asignaciones` y `model.seguimiento`:

| Dominio | Tablas | Relación central |
| --- | --- | --- |
| Académico | carreras, estudiantes, tutores_academicos | estudiante → carrera |
| Alianzas | empresas, supervisores_empresa, convenios | supervisor/convenio → empresa |
| Asignación | ofertas_practica, postulaciones, practicas | oferta → empresa, postulación → estudiante y oferta, práctica → tutor y supervisor |
| Seguimiento | bitacoras, evaluaciones, documentos | cada registro → práctica |

En **Catálogos** se accede al CRUD de las primeras seis entidades. En **Gestión de prácticas**, al CRUD de las otras seis. Los formularios cambian dinámicamente según el modelo seleccionado; los archivos FXML contienen contenedores diseñables desde Scene Builder, y los campos se construyen en `CrudController` usando `EntityCatalog`.

**CRUD completo**: crear, listar, buscar, seleccionar, editar y eliminar todas las entidades con un `JpaRepository<T>` genérico y transacciones. Claves foráneas, restricciones `UNIQUE`, `CHECK`, validación de fecha final, valores positivos, calificación y pertenencia del supervisor a la empresa. Las eliminaciones asociadas se rechazan; no se usa cascada de borrado por defecto.

**HQL**: cuatro reportes sobre entidades relacionadas, con `JOIN`, agregaciones y exportación CSV:
- Prácticas con estudiante y empresa.
- Horas acumuladas por práctica.
- Número de postulaciones por oferta.
- Promedio de evaluaciones por estudiante.

**SQL**: la creación del esquema e inserciones demostrativas se realizan con scripts; `hibernate.hbm2ddl.auto=validate` valida el mapeo y no modifica automáticamente las tablas. No hay operaciones CRUD por JDBC directo.

## Abrir y editar las pantallas en Scene Builder

Abre cualquier FXML en `src/main/resources/ni/edu/uam/practicasprofesionales/views/`. `principal.fxml` (navegación), `inicio.fxml` (tablero), `crud.fxml` (CRUD general) y `reportes.fxml` (informes). Instala Scene Builder, configura la ruta en IntelliJ **Settings → Languages & Frameworks → JavaFX** y usa **Open in Scene Builder** sobre los FXML. Ver `docs/GUIA_SCENEBUILDER.md`.

## Comprobaciones y entrega

```powershell
mvn clean compile
mvn test
mvn javafx:run
```

Para el trabajo colaborativo real consulta **`docs/REPARTO_TRABAJO_4.html`**, con responsables, pasos, comandos Git, commits, PR, revisión por otro integrante y pruebas de aceptación. Entregar vínculo GitHub, esta aplicación funcionando en IntelliJ IDEA, base PostgreSQL y demostración de CRUD/HQL. Captura evidencia conforme a `docs/EVIDENCIAS.md`.

## Seguridad

No versionar `.env`, credenciales, contraseñas personales, archivos de configuración IDE con secretos, dumps con datos sensibles ni archivos CSV exportados. `.env.example` solo contiene marcadores de ejemplo. La contraseña `1234` solicitada es únicamente para una demostración local; debe cambiarse por una más segura fuera del contexto académico.

## Límite de validación

El proyecto está entregado como código fuente preparado para ejecutar, **no como instalación ya probada contra tu PostgreSQL local**. La carga de base de datos, las pruebas de interfaz gráfica y la compilación final deben verificarse en un equipo con Maven, PostgreSQL y JavaFX disponibles.
