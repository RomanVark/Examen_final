-- SCRIPT 02. Datos DEMOSTRATIVOS: NO son personas, compañías ni credenciales reales.
-- Ejecutar luego de sql/01_esquema.sql. Puede re-ejecutarse: los ID existentes no se vuelven a crear.
BEGIN;

-- carreras: 5 ejemplos
INSERT INTO carreras (id, codigo, nombre, facultad, activa) VALUES (1, 'SIS', 'Ingeniería en Sistemas', 'Tecnología', TRUE) ON CONFLICT (id) DO NOTHING;
INSERT INTO carreras (id, codigo, nombre, facultad, activa) VALUES (2, 'ADM', 'Administración de Empresas', 'Negocios', TRUE) ON CONFLICT (id) DO NOTHING;
INSERT INTO carreras (id, codigo, nombre, facultad, activa) VALUES (3, 'MKT', 'Mercadotecnia', 'Negocios', TRUE) ON CONFLICT (id) DO NOTHING;
INSERT INTO carreras (id, codigo, nombre, facultad, activa) VALUES (4, 'IND', 'Ingeniería Industrial', 'Ingeniería', TRUE) ON CONFLICT (id) DO NOTHING;
INSERT INTO carreras (id, codigo, nombre, facultad, activa) VALUES (5, 'CON', 'Contaduría Pública', 'Economía', TRUE) ON CONFLICT (id) DO NOTHING;
SELECT setval(pg_get_serial_sequence('carreras', 'id'), GREATEST(COALESCE((SELECT MAX(id) FROM carreras), 1), 1), true);

-- estudiantes: 5 ejemplos
INSERT INTO estudiantes (id, carnet, nombres, apellidos, correo, telefono, carrera_id, activo) VALUES (1, '2026-001', 'Ana', 'López', 'ana.lopez@demo.example', '8888-1001', 1, TRUE) ON CONFLICT (id) DO NOTHING;
INSERT INTO estudiantes (id, carnet, nombres, apellidos, correo, telefono, carrera_id, activo) VALUES (2, '2026-002', 'Carlos', 'Pérez', 'carlos.perez@demo.example', '8888-1002', 2, TRUE) ON CONFLICT (id) DO NOTHING;
INSERT INTO estudiantes (id, carnet, nombres, apellidos, correo, telefono, carrera_id, activo) VALUES (3, '2026-003', 'María', 'Gómez', 'maria.gomez@demo.example', '8888-1003', 3, TRUE) ON CONFLICT (id) DO NOTHING;
INSERT INTO estudiantes (id, carnet, nombres, apellidos, correo, telefono, carrera_id, activo) VALUES (4, '2026-004', 'José', 'Martínez', 'jose.martinez@demo.example', '8888-1004', 4, TRUE) ON CONFLICT (id) DO NOTHING;
INSERT INTO estudiantes (id, carnet, nombres, apellidos, correo, telefono, carrera_id, activo) VALUES (5, '2026-005', 'Lucía', 'Ruiz', 'lucia.ruiz@demo.example', '8888-1005', 5, TRUE) ON CONFLICT (id) DO NOTHING;
SELECT setval(pg_get_serial_sequence('estudiantes', 'id'), GREATEST(COALESCE((SELECT MAX(id) FROM estudiantes), 1), 1), true);

-- empresas: 5 ejemplos
INSERT INTO empresas (id, ruc, razon_social, sector, correo, telefono, direccion, activa) VALUES (1, 'DEMO-001', 'NicaTech Solutions', 'Tecnología', 'contacto@nicatech.example', '2222-0001', 'Managua', TRUE) ON CONFLICT (id) DO NOTHING;
INSERT INTO empresas (id, ruc, razon_social, sector, correo, telefono, direccion, activa) VALUES (2, 'DEMO-002', 'Grupo Horizonte', 'Consultoría', 'rrhh@horizonte.example', '2222-0002', 'Masaya', TRUE) ON CONFLICT (id) DO NOTHING;
INSERT INTO empresas (id, ruc, razon_social, sector, correo, telefono, direccion, activa) VALUES (3, 'DEMO-003', 'Creativa Marketing', 'Publicidad', 'info@creativa.example', '2222-0003', 'Granada', TRUE) ON CONFLICT (id) DO NOTHING;
INSERT INTO empresas (id, ruc, razon_social, sector, correo, telefono, direccion, activa) VALUES (4, 'DEMO-004', 'Industria Pacífico', 'Manufactura', 'talento@pacifico.example', '2222-0004', 'León', TRUE) ON CONFLICT (id) DO NOTHING;
INSERT INTO empresas (id, ruc, razon_social, sector, correo, telefono, direccion, activa) VALUES (5, 'DEMO-005', 'Finanzas del Lago', 'Finanzas', 'contacto@lago.example', '2222-0005', 'Managua', TRUE) ON CONFLICT (id) DO NOTHING;
SELECT setval(pg_get_serial_sequence('empresas', 'id'), GREATEST(COALESCE((SELECT MAX(id) FROM empresas), 1), 1), true);

-- tutores_academicos: 5 ejemplos
INSERT INTO tutores_academicos (id, nombres, apellidos, correo, especialidad, activo) VALUES (1, 'Elena', 'Castro', 'elena.castro@demo.example', 'Desarrollo de Software', TRUE) ON CONFLICT (id) DO NOTHING;
INSERT INTO tutores_academicos (id, nombres, apellidos, correo, especialidad, activo) VALUES (2, 'Pedro', 'Romero', 'pedro.romero@demo.example', 'Administración', TRUE) ON CONFLICT (id) DO NOTHING;
INSERT INTO tutores_academicos (id, nombres, apellidos, correo, especialidad, activo) VALUES (3, 'Sofía', 'Vega', 'sofia.vega@demo.example', 'Marketing', TRUE) ON CONFLICT (id) DO NOTHING;
INSERT INTO tutores_academicos (id, nombres, apellidos, correo, especialidad, activo) VALUES (4, 'Ricardo', 'Mora', 'ricardo.mora@demo.example', 'Procesos Industriales', TRUE) ON CONFLICT (id) DO NOTHING;
INSERT INTO tutores_academicos (id, nombres, apellidos, correo, especialidad, activo) VALUES (5, 'Gabriela', 'Leiva', 'gabriela.leiva@demo.example', 'Contabilidad', TRUE) ON CONFLICT (id) DO NOTHING;
SELECT setval(pg_get_serial_sequence('tutores_academicos', 'id'), GREATEST(COALESCE((SELECT MAX(id) FROM tutores_academicos), 1), 1), true);

-- supervisores_empresa: 5 ejemplos
INSERT INTO supervisores_empresa (id, nombres, apellidos, correo, cargo, empresa_id, activo) VALUES (1, 'Roberto', 'Rivas', 'roberto.rivas@demo.example', 'Arquitecto de software', 1, TRUE) ON CONFLICT (id) DO NOTHING;
INSERT INTO supervisores_empresa (id, nombres, apellidos, correo, cargo, empresa_id, activo) VALUES (2, 'Fernanda', 'Pineda', 'fernanda.pineda@demo.example', 'Gerente de proyectos', 2, TRUE) ON CONFLICT (id) DO NOTHING;
INSERT INTO supervisores_empresa (id, nombres, apellidos, correo, cargo, empresa_id, activo) VALUES (3, 'Miguel', 'Solís', 'miguel.solis@demo.example', 'Coordinador de contenidos', 3, TRUE) ON CONFLICT (id) DO NOTHING;
INSERT INTO supervisores_empresa (id, nombres, apellidos, correo, cargo, empresa_id, activo) VALUES (4, 'Daniela', 'Flores', 'daniela.flores@demo.example', 'Jefa de planta', 4, TRUE) ON CONFLICT (id) DO NOTHING;
INSERT INTO supervisores_empresa (id, nombres, apellidos, correo, cargo, empresa_id, activo) VALUES (5, 'Álvaro', 'Núñez', 'alvaro.nunez@demo.example', 'Contador senior', 5, TRUE) ON CONFLICT (id) DO NOTHING;
SELECT setval(pg_get_serial_sequence('supervisores_empresa', 'id'), GREATEST(COALESCE((SELECT MAX(id) FROM supervisores_empresa), 1), 1), true);

-- convenios: 5 ejemplos
INSERT INTO convenios (id, codigo, empresa_id, fecha_inicio, fecha_fin, estado) VALUES (1, 'CONV-001', 1, '2026-01-01', '2027-12-31', 'VIGENTE') ON CONFLICT (id) DO NOTHING;
INSERT INTO convenios (id, codigo, empresa_id, fecha_inicio, fecha_fin, estado) VALUES (2, 'CONV-002', 2, '2026-02-01', '2027-12-31', 'VIGENTE') ON CONFLICT (id) DO NOTHING;
INSERT INTO convenios (id, codigo, empresa_id, fecha_inicio, fecha_fin, estado) VALUES (3, 'CONV-003', 3, '2026-01-15', '2027-11-30', 'VIGENTE') ON CONFLICT (id) DO NOTHING;
INSERT INTO convenios (id, codigo, empresa_id, fecha_inicio, fecha_fin, estado) VALUES (4, 'CONV-004', 4, '2026-01-01', '2027-12-31', 'VIGENTE') ON CONFLICT (id) DO NOTHING;
INSERT INTO convenios (id, codigo, empresa_id, fecha_inicio, fecha_fin, estado) VALUES (5, 'CONV-005', 5, '2026-03-01', '2027-12-31', 'VIGENTE') ON CONFLICT (id) DO NOTHING;
SELECT setval(pg_get_serial_sequence('convenios', 'id'), GREATEST(COALESCE((SELECT MAX(id) FROM convenios), 1), 1), true);

-- ofertas_practica: 5 ejemplos
INSERT INTO ofertas_practica (id, codigo, titulo, descripcion, empresa_id, cupos, fecha_inicio, fecha_fin, estado) VALUES (1, 'OF-001', 'Desarrollador JavaFX', 'Aplicación de escritorio y pruebas', 1, 2, '2026-08-01', '2026-12-15', 'ABIERTA') ON CONFLICT (id) DO NOTHING;
INSERT INTO ofertas_practica (id, codigo, titulo, descripcion, empresa_id, cupos, fecha_inicio, fecha_fin, estado) VALUES (2, 'OF-002', 'Analista de operaciones', 'Seguimiento de indicadores', 2, 2, '2026-08-10', '2026-12-10', 'ABIERTA') ON CONFLICT (id) DO NOTHING;
INSERT INTO ofertas_practica (id, codigo, titulo, descripcion, empresa_id, cupos, fecha_inicio, fecha_fin, estado) VALUES (3, 'OF-003', 'Asistente de marketing', 'Campañas digitales', 3, 3, '2026-08-15', '2026-12-12', 'ABIERTA') ON CONFLICT (id) DO NOTHING;
INSERT INTO ofertas_practica (id, codigo, titulo, descripcion, empresa_id, cupos, fecha_inicio, fecha_fin, estado) VALUES (4, 'OF-004', 'Mejora de procesos', 'Optimización de planta', 4, 1, '2026-08-05', '2026-12-20', 'CERRADA') ON CONFLICT (id) DO NOTHING;
INSERT INTO ofertas_practica (id, codigo, titulo, descripcion, empresa_id, cupos, fecha_inicio, fecha_fin, estado) VALUES (5, 'OF-005', 'Auxiliar contable', 'Conciliación y control de registros', 5, 2, '2026-08-18', '2026-12-18', 'ABIERTA') ON CONFLICT (id) DO NOTHING;
SELECT setval(pg_get_serial_sequence('ofertas_practica', 'id'), GREATEST(COALESCE((SELECT MAX(id) FROM ofertas_practica), 1), 1), true);

-- postulaciones: 7 ejemplos
INSERT INTO postulaciones (id, estudiante_id, oferta_id, fecha, estado, observaciones) VALUES (1, 1, 1, '2026-07-20', 'ACEPTADA', 'Aprobada por comité') ON CONFLICT (id) DO NOTHING;
INSERT INTO postulaciones (id, estudiante_id, oferta_id, fecha, estado, observaciones) VALUES (2, 2, 2, '2026-07-21', 'ACEPTADA', 'Entrevista aprobada') ON CONFLICT (id) DO NOTHING;
INSERT INTO postulaciones (id, estudiante_id, oferta_id, fecha, estado, observaciones) VALUES (3, 3, 3, '2026-07-23', 'ACEPTADA', 'Perfil compatible') ON CONFLICT (id) DO NOTHING;
INSERT INTO postulaciones (id, estudiante_id, oferta_id, fecha, estado, observaciones) VALUES (4, 4, 4, '2026-07-24', 'ACEPTADA', 'Validada por empresa') ON CONFLICT (id) DO NOTHING;
INSERT INTO postulaciones (id, estudiante_id, oferta_id, fecha, estado, observaciones) VALUES (5, 5, 5, '2026-07-25', 'ACEPTADA', 'Documentación completa') ON CONFLICT (id) DO NOTHING;
INSERT INTO postulaciones (id, estudiante_id, oferta_id, fecha, estado, observaciones) VALUES (6, 1, 2, '2026-07-27', 'PENDIENTE', 'En revisión') ON CONFLICT (id) DO NOTHING;
INSERT INTO postulaciones (id, estudiante_id, oferta_id, fecha, estado, observaciones) VALUES (7, 3, 1, '2026-07-28', 'RECHAZADA', 'Plaza cubierta') ON CONFLICT (id) DO NOTHING;
SELECT setval(pg_get_serial_sequence('postulaciones', 'id'), GREATEST(COALESCE((SELECT MAX(id) FROM postulaciones), 1), 1), true);

-- practicas: 5 ejemplos
INSERT INTO practicas (id, estudiante_id, oferta_id, tutor_academico_id, supervisor_empresa_id, fecha_inicio, fecha_fin, horas_requeridas, estado) VALUES (1, 1, 1, 1, 1, '2026-08-01', '2026-12-15', 240, 'EN_CURSO') ON CONFLICT (id) DO NOTHING;
INSERT INTO practicas (id, estudiante_id, oferta_id, tutor_academico_id, supervisor_empresa_id, fecha_inicio, fecha_fin, horas_requeridas, estado) VALUES (2, 2, 2, 2, 2, '2026-08-10', '2026-12-10', 240, 'EN_CURSO') ON CONFLICT (id) DO NOTHING;
INSERT INTO practicas (id, estudiante_id, oferta_id, tutor_academico_id, supervisor_empresa_id, fecha_inicio, fecha_fin, horas_requeridas, estado) VALUES (3, 3, 3, 3, 3, '2026-08-15', '2026-12-12', 200, 'EN_CURSO') ON CONFLICT (id) DO NOTHING;
INSERT INTO practicas (id, estudiante_id, oferta_id, tutor_academico_id, supervisor_empresa_id, fecha_inicio, fecha_fin, horas_requeridas, estado) VALUES (4, 4, 4, 4, 4, '2026-08-05', '2026-12-20', 280, 'EN_CURSO') ON CONFLICT (id) DO NOTHING;
INSERT INTO practicas (id, estudiante_id, oferta_id, tutor_academico_id, supervisor_empresa_id, fecha_inicio, fecha_fin, horas_requeridas, estado) VALUES (5, 5, 5, 5, 5, '2026-08-18', '2026-12-18', 200, 'EN_CURSO') ON CONFLICT (id) DO NOTHING;
SELECT setval(pg_get_serial_sequence('practicas', 'id'), GREATEST(COALESCE((SELECT MAX(id) FROM practicas), 1), 1), true);

-- bitacoras: 7 ejemplos
INSERT INTO bitacoras (id, practica_id, fecha, actividad, horas, validada) VALUES (1, 1, '2026-09-02', 'Diseño de entidades JPA', 6, TRUE) ON CONFLICT (id) DO NOTHING;
INSERT INTO bitacoras (id, practica_id, fecha, actividad, horas, validada) VALUES (2, 1, '2026-09-03', 'Integración JavaFX y Hibernate', 7, TRUE) ON CONFLICT (id) DO NOTHING;
INSERT INTO bitacoras (id, practica_id, fecha, actividad, horas, validada) VALUES (3, 2, '2026-09-02', 'Análisis de reportes gerenciales', 8, TRUE) ON CONFLICT (id) DO NOTHING;
INSERT INTO bitacoras (id, practica_id, fecha, actividad, horas, validada) VALUES (4, 3, '2026-09-05', 'Diseño de piezas digitales', 5, TRUE) ON CONFLICT (id) DO NOTHING;
INSERT INTO bitacoras (id, practica_id, fecha, actividad, horas, validada) VALUES (5, 4, '2026-09-08', 'Revisión de procedimiento', 8, FALSE) ON CONFLICT (id) DO NOTHING;
INSERT INTO bitacoras (id, practica_id, fecha, actividad, horas, validada) VALUES (6, 5, '2026-09-10', 'Conciliación de cuentas', 6, TRUE) ON CONFLICT (id) DO NOTHING;
INSERT INTO bitacoras (id, practica_id, fecha, actividad, horas, validada) VALUES (7, 2, '2026-09-11', 'Elaboración de informe', 7, FALSE) ON CONFLICT (id) DO NOTHING;
SELECT setval(pg_get_serial_sequence('bitacoras', 'id'), GREATEST(COALESCE((SELECT MAX(id) FROM bitacoras), 1), 1), true);

-- evaluaciones: 5 ejemplos
INSERT INTO evaluaciones (id, practica_id, fecha, evaluador, calificacion, observaciones) VALUES (1, 1, '2026-10-10', 'Tutor académico', 92.5, 'Buen desempeño') ON CONFLICT (id) DO NOTHING;
INSERT INTO evaluaciones (id, practica_id, fecha, evaluador, calificacion, observaciones) VALUES (2, 2, '2026-10-11', 'Supervisor de empresa', 89.0, 'Cumple objetivos') ON CONFLICT (id) DO NOTHING;
INSERT INTO evaluaciones (id, practica_id, fecha, evaluador, calificacion, observaciones) VALUES (3, 3, '2026-10-12', 'Tutor académico', 95.0, 'Excelente iniciativa') ON CONFLICT (id) DO NOTHING;
INSERT INTO evaluaciones (id, practica_id, fecha, evaluador, calificacion, observaciones) VALUES (4, 4, '2026-10-13', 'Supervisor de empresa', 86.0, 'Mejorar puntualidad') ON CONFLICT (id) DO NOTHING;
INSERT INTO evaluaciones (id, practica_id, fecha, evaluador, calificacion, observaciones) VALUES (5, 5, '2026-10-14', 'Tutor académico', 91.0, 'Trabajo sólido') ON CONFLICT (id) DO NOTHING;
SELECT setval(pg_get_serial_sequence('evaluaciones', 'id'), GREATEST(COALESCE((SELECT MAX(id) FROM evaluaciones), 1), 1), true);

-- documentos: 5 ejemplos
INSERT INTO documentos (id, practica_id, tipo, nombre_archivo, ruta_archivo, fecha_entrega, verificado) VALUES (1, 1, 'PLAN', 'plan_ana.pdf', 'documentos/plan_ana.pdf', '2026-08-01', TRUE) ON CONFLICT (id) DO NOTHING;
INSERT INTO documentos (id, practica_id, tipo, nombre_archivo, ruta_archivo, fecha_entrega, verificado) VALUES (2, 2, 'CARTA', 'carta_carlos.pdf', 'documentos/carta_carlos.pdf', '2026-08-11', TRUE) ON CONFLICT (id) DO NOTHING;
INSERT INTO documentos (id, practica_id, tipo, nombre_archivo, ruta_archivo, fecha_entrega, verificado) VALUES (3, 3, 'PLAN', 'plan_maria.pdf', 'documentos/plan_maria.pdf', '2026-08-16', TRUE) ON CONFLICT (id) DO NOTHING;
INSERT INTO documentos (id, practica_id, tipo, nombre_archivo, ruta_archivo, fecha_entrega, verificado) VALUES (4, 4, 'CONVENIO', 'convenio_jose.pdf', 'documentos/convenio_jose.pdf', '2026-08-06', FALSE) ON CONFLICT (id) DO NOTHING;
INSERT INTO documentos (id, practica_id, tipo, nombre_archivo, ruta_archivo, fecha_entrega, verificado) VALUES (5, 5, 'INFORME', 'informe_lucia.pdf', 'documentos/informe_lucia.pdf', '2026-10-20', FALSE) ON CONFLICT (id) DO NOTHING;
SELECT setval(pg_get_serial_sequence('documentos', 'id'), GREATEST(COALESCE((SELECT MAX(id) FROM documentos), 1), 1), true);

COMMIT;
