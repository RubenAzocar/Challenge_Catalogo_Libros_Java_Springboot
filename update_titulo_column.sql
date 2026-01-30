-- Script para actualizar la columna titulo en la tabla libros
-- Ejecutar esto si Hibernate no actualiza automáticamente

ALTER TABLE libros ALTER COLUMN titulo TYPE VARCHAR(500);
