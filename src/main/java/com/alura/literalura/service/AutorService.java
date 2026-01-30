package com.alura.literalura.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.alura.literalura.model.Autor;
import com.alura.literalura.repository.AutorRepository;

@Service
public class AutorService {

    private final AutorRepository autorRepository;

    public AutorService(AutorRepository autorRepository) {
        this.autorRepository = autorRepository;
    }

    public List<Autor> listarTodosLosAutores() {
        return autorRepository.findAllOrderByNombre();
    }

    public List<Autor> listarAutoresVivosEnAnio(Integer anio) {
        // Validar año (debe ser razonable)
        if (anio == null) {
            System.out.println("\n❌ El año no puede ser nulo.");
            return List.of();
        }

        if (anio < -3000 || anio > 2100) {
            System.out.println("\n❌ El año debe estar entre -3000 y 2100.");
            return List.of();
        }

        List<Autor> autores = autorRepository.findAutoresVivosEnAnio(anio);

        if (autores.isEmpty()) {
            System.out.println("\n⚠️ No se encontraron autores vivos en el año " + anio);
        }

        return autores;
    }
}
