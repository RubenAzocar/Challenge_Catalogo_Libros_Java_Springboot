package com.alura.literalura.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.alura.literalura.model.Libro;

@Repository
public interface LibroRepository extends JpaRepository<Libro, Long> {

    // Buscar libro por título (evitar duplicados)
    Optional<Libro> findByTituloIgnoreCase(String titulo);

    // Buscar libros por código de idioma (case-insensitive)
    @Query("SELECT l FROM Libro l JOIN l.idiomas i WHERE LOWER(i) = LOWER(:idioma)")
    List<Libro> findByIdioma(@Param("idioma") String idioma);

    // Listar todos los libros ordenados por número de descargas (NULLS LAST)
    @Query("SELECT l FROM Libro l ORDER BY l.numeroDescargas DESC NULLS LAST")
    List<Libro> findAllOrderByDescargas();
}
