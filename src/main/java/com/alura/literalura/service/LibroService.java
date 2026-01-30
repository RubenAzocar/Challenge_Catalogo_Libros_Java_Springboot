package com.alura.literalura.service;

import java.util.List;
import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alura.literalura.dto.DatosAutor;
import com.alura.literalura.dto.DatosLibro;
import com.alura.literalura.dto.DatosRespuesta;
import com.alura.literalura.model.Autor;
import com.alura.literalura.model.Libro;
import com.alura.literalura.repository.AutorRepository;
import com.alura.literalura.repository.LibroRepository;

@Service
public class LibroService {

    private final LibroRepository libroRepository;
    private final AutorRepository autorRepository;

    private final ConsumoAPI consumoAPI = new ConsumoAPI();
    private final ConvierteDatos conversor = new ConvierteDatos();

    public LibroService(LibroRepository libroRepository, AutorRepository autorRepository) {
        this.libroRepository = libroRepository;
        this.autorRepository = autorRepository;
    }

    @Transactional
    public Libro buscarYGuardarLibroPorTitulo(String titulo) {
        String tituloBusqueda = (titulo == null) ? "" : titulo.trim();

        // 1) Check rápido con lo que escribió el usuario (si justo coincide)
        Optional<Libro> libroExistente = libroRepository.findByTituloIgnoreCase(tituloBusqueda);
        if (libroExistente.isPresent()) {
            System.out.println("\n⚠️ El libro ya está registrado en la base de datos.");
            return libroExistente.get();
        }

        try {
            String json = consumoAPI.buscarLibroPorTitulo(tituloBusqueda);
            DatosRespuesta respuesta = conversor.obtenerDatos(json, DatosRespuesta.class);

            if (respuesta.resultados() == null || respuesta.resultados().isEmpty()) {
                System.out.println("\n❌ No se encontraron resultados para: " + tituloBusqueda);
                return null;
            }

            DatosLibro datosLibro = respuesta.resultados().get(0);

            // 2) Título oficial devuelto por la API (este es el que realmente guardas)
            String tituloApi = (datosLibro.titulo() == null) ? "" : datosLibro.titulo().trim();
            if (tituloApi.isBlank()) {
                System.out.println("\n❌ La API devolvió un libro sin título válido.");
                return null;
            }

            // Truncar el título si es demasiado largo (máximo 500 caracteres)
            if (tituloApi.length() > 500) {
                tituloApi = tituloApi.substring(0, 497) + "...";
                System.out.println("\n⚠️ El título fue truncado por ser demasiado largo.");
            }

            // 3) Check por título oficial para evitar el UNIQUE violation
            Optional<Libro> existentePorTituloApi = libroRepository.findByTituloIgnoreCase(tituloApi);
            if (existentePorTituloApi.isPresent()) {
                System.out.println("\n⚠️ El libro ya está registrado en la base de datos (por título oficial).");
                return existentePorTituloApi.get();
            }

            Autor autor = null;
            if (datosLibro.autores() != null && !datosLibro.autores().isEmpty()) {
                DatosAutor datosAutor = datosLibro.autores().get(0);
                autor = obtenerOCrearAutor(datosAutor);
            }

            // Validar y limpiar idiomas
            List<String> idiomas = datosLibro.idiomas();
            if (idiomas == null || idiomas.isEmpty()) {
                idiomas = List.of("desconocido");
            } else {
                // Normalizar idiomas a minúsculas
                idiomas = idiomas.stream()
                        .filter(i -> i != null && !i.trim().isEmpty())
                        .map(String::toLowerCase)
                        .toList();

                if (idiomas.isEmpty()) {
                    idiomas = List.of("desconocido");
                }
            }

            // Validar número de descargas
            Integer numeroDescargas = datosLibro.numeroDescargas();
            if (numeroDescargas == null || numeroDescargas < 0) {
                numeroDescargas = 0;
            }

            Libro libro = new Libro(tituloApi, autor, idiomas, numeroDescargas);

            // 4) Guardar con "red de seguridad" por si hay carrera/concurrencia
            try {
                libro = libroRepository.save(libro);
                System.out.println("\n✅ Libro guardado exitosamente!");
                return libro;
            } catch (DataIntegrityViolationException ex) {
                Optional<Libro> yaExiste = libroRepository.findByTituloIgnoreCase(tituloApi);
                if (yaExiste.isPresent()) {
                    System.out.println("\n⚠️ El libro ya estaba registrado (se detectó al guardar).");
                    return yaExiste.get();
                }
                throw ex;
            }

        } catch (Exception e) {
            System.out.println("\n❌ Error al buscar el libro: " + e.getMessage());
            return null;
        }
    }

    @Transactional
    protected Autor obtenerOCrearAutor(DatosAutor datosAutor) {
        // Validar que el nombre no sea nulo o vacío
        if (datosAutor == null || datosAutor.nombre() == null || datosAutor.nombre().trim().isEmpty()) {
            System.out.println("⚠️ Autor sin nombre válido, se omitirá.");
            return null;
        }

        String nombreAutor = datosAutor.nombre().trim();

        // Truncar nombre si es muy largo
        if (nombreAutor.length() > 300) {
            nombreAutor = nombreAutor.substring(0, 297) + "...";
        }

        Optional<Autor> autorExistente = autorRepository.findByNombreIgnoreCase(nombreAutor);
        if (autorExistente.isPresent()) {
            return autorExistente.get();
        }

        // Validar años (evitar valores absurdos)
        Integer anioNacimiento = datosAutor.anioNacimiento();
        Integer anioFallecimiento = datosAutor.anioFallecimiento();

        if (anioNacimiento != null && (anioNacimiento < -3000 || anioNacimiento > 2100)) {
            anioNacimiento = null;
        }

        if (anioFallecimiento != null && (anioFallecimiento < -3000 || anioFallecimiento > 2100)) {
            anioFallecimiento = null;
        }

        // Validar lógica: fallecimiento debe ser después del nacimiento
        if (anioNacimiento != null && anioFallecimiento != null && anioFallecimiento < anioNacimiento) {
            System.out.println("⚠️ Año de fallecimiento incoherente para " + nombreAutor + ", se omitirá.");
            anioFallecimiento = null;
        }

        Autor nuevoAutor = new Autor(nombreAutor, anioNacimiento, anioFallecimiento);
        return autorRepository.save(nuevoAutor);
    }

    public List<Libro> listarTodosLosLibros() {
        return libroRepository.findAllOrderByDescargas();
    }

    public List<Libro> listarLibrosPorIdioma(String codigoIdioma) {
        // Validar código de idioma
        if (codigoIdioma == null || codigoIdioma.trim().isEmpty()) {
            System.out.println("\n❌ El código de idioma no puede estar vacío.");
            return List.of();
        }

        String idiomaLimpio = codigoIdioma.trim().toLowerCase();

        // Validar formato (generalmente 2 letras: es, en, fr, pt, etc.)
        if (idiomaLimpio.length() > 10) {
            System.out.println("\n❌ El código de idioma no es válido.");
            return List.of();
        }

        List<Libro> libros = libroRepository.findByIdioma(idiomaLimpio);

        if (libros.isEmpty()) {
            System.out.println("\n⚠️ No se encontraron libros en el idioma: " + idiomaLimpio);
        }

        return libros;
    }
}
