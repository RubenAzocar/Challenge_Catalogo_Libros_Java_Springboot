package com.alura.literalura.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.alura.literalura.model.Autor;

@Repository
public interface AutorRepository extends JpaRepository<Autor, Long> {

    // Buscar autor por nombre (evitar duplicados)
    Optional<Autor> findByNombreIgnoreCase(String nombre);

    // Buscar autores vivos en un año específico
    // Un autor está vivo en un año si:
    // 1. Ya había nacido (anioNacimiento <= año buscado)
    // 2. Y todavía no había muerto (anioFallecimiento IS NULL O anioFallecimiento >= año buscado)
    @Query("SELECT a FROM Autor a WHERE " +
           "a.anioNacimiento IS NOT NULL AND a.anioNacimiento <= :anio AND " +
           "(a.anioFallecimiento IS NULL OR a.anioFallecimiento >= :anio)")
    List<Autor> findAutoresVivosEnAnio(@Param("anio") Integer anio);

    // Listar todos los autores ordenados por nombre
    @Query("SELECT a FROM Autor a ORDER BY a.nombre ASC")
    List<Autor> findAllOrderByNombre();
}
