-- Ejecutar sobre la base practicas_profesionales ya existente, sin borrar registros.
BEGIN;
CREATE TABLE IF NOT EXISTS carrera_tutor (
    carrera_id BIGINT NOT NULL REFERENCES carreras(id) ON DELETE CASCADE,
    tutor_id BIGINT NOT NULL REFERENCES tutores_academicos(id) ON DELETE CASCADE,
    PRIMARY KEY (carrera_id, tutor_id)
);
CREATE INDEX IF NOT EXISTS idx_carrera_tutor_tutor_id ON carrera_tutor(tutor_id);
COMMIT;
