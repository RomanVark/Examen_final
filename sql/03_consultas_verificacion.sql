-- SCRIPT 03: consultas SQL para verificar datos creados y relaciones.
SELECT 'carreras' AS tabla, count(*) FROM carreras
UNION ALL SELECT 'estudiantes', count(*) FROM estudiantes
UNION ALL SELECT 'empresas', count(*) FROM empresas
UNION ALL SELECT 'tutores_academicos', count(*) FROM tutores_academicos
UNION ALL SELECT 'supervisores_empresa', count(*) FROM supervisores_empresa
UNION ALL SELECT 'convenios', count(*) FROM convenios
UNION ALL SELECT 'ofertas_practica', count(*) FROM ofertas_practica
UNION ALL SELECT 'postulaciones', count(*) FROM postulaciones
UNION ALL SELECT 'practicas', count(*) FROM practicas
UNION ALL SELECT 'bitacoras', count(*) FROM bitacoras
UNION ALL SELECT 'evaluaciones', count(*) FROM evaluaciones
UNION ALL SELECT 'documentos', count(*) FROM documentos;

-- Prácticas unidas con sus estudiantes y empresas:
SELECT p.id, e.carnet, e.nombres || ' ' || e.apellidos AS estudiante,
       em.razon_social AS empresa, p.estado
FROM practicas p
JOIN estudiantes e ON e.id = p.estudiante_id
JOIN ofertas_practica o ON o.id = p.oferta_id
JOIN empresas em ON em.id = o.empresa_id
ORDER BY p.id;

-- Horas acumuladas por práctica:
SELECT p.id, e.carnet, COALESCE(SUM(b.horas),0) AS horas_registradas,
       p.horas_requeridas
FROM practicas p JOIN estudiantes e ON e.id=p.estudiante_id
LEFT JOIN bitacoras b ON b.practica_id=p.id
GROUP BY p.id,e.carnet,p.horas_requeridas ORDER BY p.id;

-- Postulaciones por oferta:
SELECT o.codigo, o.titulo, COUNT(po.id) AS postulaciones
FROM ofertas_practica o LEFT JOIN postulaciones po ON po.oferta_id=o.id
GROUP BY o.id ORDER BY o.id;
