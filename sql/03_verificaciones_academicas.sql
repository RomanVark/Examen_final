-- Ejecutar en practicas_profesionales para verificar datos relacionados.
SELECT c.codigo,c.nombre,COUNT(e.id) AS estudiantes
FROM carreras c LEFT JOIN estudiantes e ON e.carrera_id=c.id
GROUP BY c.id,c.codigo,c.nombre ORDER BY c.codigo;

SELECT e.carnet,e.nombres,e.apellidos,e.correo,c.nombre AS carrera
FROM estudiantes e JOIN carreras c ON c.id=e.carrera_id
ORDER BY e.carnet;

SELECT id,nombres,apellidos,correo,activo FROM tutores_academicos ORDER BY id;

-- Controles de unicidad en PostgreSQL (mostrarán error si se intentan):
-- INSERT INTO estudiantes(carnet,nombres,apellidos,correo,carrera_id,activo)
-- VALUES('2026-001','Duplicado','Prueba','duplicado@demo.example',1,true);
-- INSERT INTO tutores_academicos(nombres,apellidos,correo,especialidad,activo)
-- VALUES('Duplicado','Tutor','sofia.ortega@demo.example','Prueba',true);
