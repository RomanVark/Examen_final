-- 15 registros ficticios: 5 carreras, 5 estudiantes y 5 tutores.
-- Ejecutar en practicas_profesionales después de 01_academico.sql.
BEGIN;
INSERT INTO carreras(id,codigo,nombre,facultad,activa) VALUES
(1,'SIS','Ingeniería en Sistemas','Tecnología',true),
(2,'ADM','Administración de Empresas','Negocios',true),
(3,'MKT','Mercadotecnia','Negocios',true),
(4,'IND','Ingeniería Industrial','Ingeniería',true),
(5,'CON','Contaduría Pública','Economía',true)
ON CONFLICT(id) DO NOTHING;

INSERT INTO estudiantes(id,carnet,nombres,apellidos,correo,telefono,carrera_id,activo) VALUES
(1,'2026-001','Ana','López','ana.lopez@demo.example','8888-1001',1,true),
(2,'2026-002','Carlos','Pérez','carlos.perez@demo.example','8888-1002',2,true),
(3,'2026-003','María','Gómez','maria.gomez@demo.example','8888-1003',3,true),
(4,'2026-004','José','Martínez','jose.martinez@demo.example','8888-1004',4,true),
(5,'2026-005','Lucía','Ruiz','lucia.ruiz@demo.example','8888-1005',5,true)
ON CONFLICT(id) DO NOTHING;

INSERT INTO tutores_academicos(id,nombres,apellidos,correo,especialidad,activo) VALUES
(1,'Sofía','Ortega','sofia.ortega@demo.example','Desarrollo de software',true),
(2,'Ricardo','Navarro','ricardo.navarro@demo.example','Gestión empresarial',true),
(3,'Elena','Castro','elena.castro@demo.example','Marketing digital',true),
(4,'Javier','Morales','javier.morales@demo.example','Ingeniería de procesos',true),
(5,'Patricia','Vargas','patricia.vargas@demo.example','Auditoría',true)
ON CONFLICT(id) DO NOTHING;

SELECT setval(pg_get_serial_sequence('carreras','id'), GREATEST(COALESCE((SELECT MAX(id) FROM carreras),1),1),true);
SELECT setval(pg_get_serial_sequence('estudiantes','id'), GREATEST(COALESCE((SELECT MAX(id) FROM estudiantes),1),1),true);
SELECT setval(pg_get_serial_sequence('tutores_academicos','id'), GREATEST(COALESCE((SELECT MAX(id) FROM tutores_academicos),1),1),true);
COMMIT;
