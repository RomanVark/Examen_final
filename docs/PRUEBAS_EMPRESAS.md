# Verificación del CRUD de empresas y sus referencias

Estas pruebas se realizan desde la pantalla **Administración de registros** con la aplicación conectada a PostgreSQL. Se usan registros de prueba para no modificar los datos de ejemplo.

## 1. Alta de una empresa

1. Seleccionar **Empresa** en el selector de módulos.
2. Crear una empresa con estos datos:
   - RUC: `QA-EMP-2026-001`
   - Razón social: `Empresa QA Uno`
   - Sector: `Tecnología`
   - Correo: `qa.empresa1@example.com`
   - Activa: `Sí`
3. Guardar y comprobar que aparece en la tabla.

**Resultado esperado:** el registro se guarda y aparece una sola vez.

## 2. Edición de la misma empresa

1. Seleccionar `Empresa QA Uno` en la tabla.
2. Cambiar la razón social a `Empresa QA Uno Editada`.
3. Mantener el mismo RUC y guardar.

**Resultado esperado:** se actualiza el registro existente. No debe aparecer un error de RUC duplicado al conservar el RUC de esa misma empresa.

## 3. Rechazo de RUC duplicado

1. Crear otra empresa con un nombre diferente.
2. Escribir el RUC `qa-emp-2026-001` (misma clave, en minúsculas).
3. Completar los demás campos y guardar.

**Resultado esperado:** el sistema rechaza el guardado e informa que ya existe una empresa con ese RUC. La comparación no debe depender de mayúsculas ni de espacios externos.

## 4. Validación de convenios

1. Seleccionar **Convenio** y crear uno asociado a `Empresa QA Uno Editada`.
2. Usar un código único, fecha de inicio de ayer y fecha final dentro de los próximos 30 días, con estado `VIGENTE`.
3. Guardar. Después, intentar otro convenio `VIGENTE` cuya fecha final sea anterior a hoy.

**Resultado esperado:** el convenio dentro de fechas se guarda; el convenio `VIGENTE` fuera de su período se rechaza. También debe rechazarse una fecha final anterior a la fecha inicial.

## 5. Eliminación sin referencias

1. Crear una empresa nueva con RUC `QA-EMP-2026-002`.
2. No asociarla a supervisores, convenios ni ofertas.
3. Seleccionarla y pulsar **Eliminar**.

**Resultado esperado:** después de confirmar, la empresa desaparece de la tabla.

## 6. Bloqueo de eliminación por referencias

1. Seleccionar **Supervisor de empresa** y crear un supervisor asociado a `Empresa QA Uno Editada`.
2. Seleccionar **Convenio** y crear un convenio válido asociado a esa misma empresa.
3. Volver a **Empresa**, seleccionar `Empresa QA Uno Editada` e intentar eliminarla.

**Resultado esperado:** PostgreSQL impide la eliminación y la aplicación informa que el registro está asociado a otros datos. La empresa debe permanecer en la tabla. Las claves foráneas usan `ON DELETE RESTRICT`; no se deben borrar supervisores ni convenios automáticamente.

## 7. Limpieza de los datos de prueba

Eliminar primero el supervisor y el convenio creados en la prueba 6. Después, eliminar `Empresa QA Uno Editada`.

**Resultado esperado:** una vez retiradas las referencias, la empresa se puede eliminar.
