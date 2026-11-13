# Guía de interfaces JavaFX con Scene Builder — Rama Roman

Esta guía corresponde a la **fase 2 de interfaz**. Las pantallas están separadas
en archivos FXML y usan CSS. Se mantienen el CRUD genérico y los controladores de cada integrante.

## Requisitos

- Temurin JDK 21.0.12 en IntelliJ IDEA.
- JavaFX 21 a través de Maven.
- Gluon Scene Builder compatible con JavaFX 21.

En IntelliJ: **Settings → Languages & Frameworks → JavaFX → Path to SceneBuilder**.

## Pantallas y navegación

Ruta base: `src/main/resources/ni/edu/uam/practicasprofesionales/`.

| Pantalla editable | Controlador | Menú que la abre |
| --- | --- | --- |
| `views/principal.fxml` | `PrincipalController` | Estructura de toda la aplicación |
| `views/inicio.fxml` | `InicioController` | Inicio / Resumen |
| `views/academico.fxml` | `AcademicoController` | Módulo académico |
| `views/crud.fxml` | `CrudController` | Catálogos y Gestión de prácticas |
| `views/reportes.fxml` | `ReportesController` | Reportes HQL |
| `seguimiento-reportes.fxml` | `SeguimientoReportesController` | Seguimiento / Reportes |

**No elimines los `fx:id`, `fx:controller` ni `onAction`.**
La navegación se encuentra en `PrincipalController` y se cierra la sesión
de seguimiento al abandonar esa pantalla o al finalizar la aplicación.

## Qué se puede editar visualmente

- **Principal:** orden, textos y distribución de los botones del menú; mantener las acciones.
- **Académico:** sus formularios y tablas están definidos en `academico.fxml`;
  colores y espaciado en las clases `academic-*` del CSS.
- **CRUD general:** Scene Builder permite editar la estructura, tabla,
  filtro y botones. Los **campos específicos se generan en Java** por
  `CrudController` y `EntityCatalog`; no aparecen como controles separados
  en Scene Builder.
- **Reportes HQL y seguimiento:** encabezados, selector, tabla y botones
  definidos en sus FXML; la información se carga desde los controladores.

Los dos `TableView` del CRUD y de reportes ya utilizan
`<placeholder><Label .../></placeholder>`; no convertirlo a un atributo de texto.

## Pruebas de esta fase

Se añadió `src/test/java/ni/edu/uam/practicasprofesionales/ui/InterfazFxmlTest.java`.
Comprueba la correspondencia de `fx:id` y `onAction` con los controladores;
también construye las seis pantallas sin conexión a base de datos bajo Xvfb.

La compilación y estas pruebas se ejecutan automáticamente en la rama `Roman`
desde `.github/workflows/compilar-roman.yml`. Para validar localmente:

```powershell
$env:DB_URL="jdbc:postgresql://localhost:5432/practicas_profesionales"
$env:DB_USER="postgres"
$env:DB_PASSWORD="1234"  # Solo para el laboratorio local
.\mvnw.cmd clean test
.\mvnw.cmd javafx:run
```

**Pruebas manuales pendientes con PostgreSQL real:**

1. Abrir cada opción del menú y regresar a Inicio.
2. Crear y editar una carrera, estudiante y tutor desde la pantalla académica.
3. Abrir Catálogos y Gestión de prácticas y comprobar sus formularios.
4. Abrir los reportes generales y los de seguimiento; comprobar HQL y exportación CSV.
5. Editar `academico.fxml` en Scene Builder, guardar y volver a ejecutar.
6. Capturar evidencia de navegación, formularios, validaciones y reportes.

Las pruebas automáticas de FXML construyen los nodos **sin ejecutar los controladores**
para no depender de PostgreSQL; no sustituyen estas verificaciones manuales.

## Límites de la fase 2

No se cambiaron entidades JPA, reglas de negocio, scripts SQL ni las otras ramas.
La fase 3 cubrirá la validación de persistencia y el funcionamiento CRUD integral.
