# Módulo de asignaciones — Alfredo Montealegre

**Rama de trabajo:** `feat/asignaciones-alfredo`

Este módulo utiliza las entidades, el CRUD genérico, la persistencia JPA y la estructura JavaFX que ya existen en el proyecto. No crea una segunda estructura de aplicación ni reemplaza los modelos ya entregados.

## Alcance según `docs/REPARTO_TRABAJO_4.html`

- `OfertaPractica`, `Postulacion` y `Practica`.
- Comprobación de claves foráneas y unicidad de estudiante + oferta.
- Validación de cupos, fechas y correspondencia entre supervisor y empresa.
- Dos reportes HQL: prácticas por estudiante/empresa y postulaciones por oferta.
- Documentación y pruebas de la transición de postulación aceptada a práctica asignada.

## Cambios realizados

### 1. Entidades y claves foráneas

Las tres entidades ya estaban presentes en `model/asignaciones` y ya estaban registradas en `ui/EntityCatalog`, por lo que se conservaron. Sus relaciones `@ManyToOne` y restricciones `@UniqueConstraint` corresponden a las tablas existentes. El script `sql/01_esquema.sql` ya define las claves foráneas y restricciones `UNIQUE` para estudiante + oferta; se conserva sin duplicar tablas ni cambiar nombres.

### 2. Validaciones

Se amplió `repository/ValidacionDeNegocio.java`, conservando las validaciones previas de empresas, convenios, bitácoras y evaluaciones. Para asignaciones se valida:

- oferta con empresa registrada, estado, cupos positivos y fechas en orden;
- postulación con estudiante y oferta registrados, oferta ABIERTA y fecha no futura;
- práctica con estudiante, oferta, tutor y supervisor registrados;
- supervisor activo y perteneciente a la empresa de la oferta;
- fechas de práctica dentro del periodo de la oferta y horas positivas;
- unicidad de la combinación estudiante + oferta en postulaciones y prácticas.

La base de datos mantiene las restricciones de integridad como segunda barrera.

### 3. Consultas HQL

`service/ReporteHqlService.java` conserva los cuatro reportes del proyecto. Los dos reportes que corresponden a este módulo son:

- **Prácticas por estudiante y empresa:** une práctica, estudiante, oferta y empresa.
- **Postulaciones por oferta:** agrupa ofertas y cuenta postulaciones, incluyendo ofertas sin postulaciones mediante `left join`.

### 4. Transición de postulación a práctica

Se agregó `service/AsignacionesService.java` como caso de uso para la transición. `aceptarYAsignar(...)`:

1. Busca la postulación y exige estado PENDIENTE.
2. Comprueba que la oferta siga ABIERTA.
3. Cuenta prácticas asignadas a la oferta y verifica que no excedan los cupos.
4. Comprueba tutor y supervisor.
5. Construye la práctica con fechas de la oferta y estado PLANIFICADA.
6. Ejecuta las validaciones de negocio y unicidad.
7. Cambia la postulación a ACEPTADA y persiste la práctica dentro de una sola transacción.

Si una validación falla, se revierte la transacción para evitar que la postulación quede aceptada sin una práctica creada.

## Pruebas

`src/test/java/.../repository/AsignacionesValidacionTest.java` cubre casos positivos y negativos de cupos, fechas, referencias, estado de oferta, supervisor y horas.

La transición que consulta y modifica PostgreSQL requiere una base de datos configurada. Debe comprobarse manualmente con una postulación PENDIENTE, oferta ABIERTA con cupos y tutor/supervisor válidos. Después debe verificarse que la postulación quede ACEPTADA y exista una práctica PLANIFICADA. También debe probarse una oferta sin cupos y un supervisor de otra empresa.

## Validación local

Desde la raíz del proyecto:

```bash
mvn clean test
mvn clean compile
mvn javafx:run
```

Para ejecutar la aplicación, configurar `DB_URL`, `DB_USER` y `DB_PASSWORD` como indica el README existente y ejecutar previamente los scripts SQL del proyecto.

## Archivos modificados / añadidos

- Modificado: `src/main/java/ni/edu/uam/practicasprofesionales/repository/ValidacionDeNegocio.java`
- Modificado: `src/main/java/ni/edu/uam/practicasprofesionales/service/ReporteHqlService.java`
- Añadido: `src/main/java/ni/edu/uam/practicasprofesionales/service/AsignacionesService.java`
- Añadido: `src/test/java/ni/edu/uam/practicasprofesionales/repository/AsignacionesValidacionTest.java`
- Añadido: `docs/ASIGNACIONES_ALFREDO.md`

No se modificaron `pom.xml`, FXML, CSS, controladores, `EntityCatalog`, ni el esquema SQL porque la base del proyecto ya contempla las tres entidades y su CRUD genérico.
