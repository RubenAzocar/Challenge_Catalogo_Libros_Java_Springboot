package com.alura.literalura.principal;

import java.util.List;
import java.util.Scanner;

import org.springframework.stereotype.Component;

import com.alura.literalura.model.Autor;
import com.alura.literalura.model.Libro;
import com.alura.literalura.service.AutorService;
import com.alura.literalura.service.LibroService;

@Component
public class Principal {

    private final LibroService libroService;
    private final AutorService autorService;

    private final Scanner scanner = new Scanner(System.in);

    public Principal(LibroService libroService, AutorService autorService) {
        this.libroService = libroService;
        this.autorService = autorService;
    }

    public void muestraElMenu() {
        int opcion = -1;

        while (opcion != 0) {
            mostrarMenu();

            try {
                opcion = Integer.parseInt(scanner.nextLine());

                switch (opcion) {
                    case 1 -> buscarLibroPorTitulo();
                    case 2 -> listarLibrosRegistrados();
                    case 3 -> listarAutoresRegistrados();
                    case 4 -> listarAutoresVivosPorAnio();
                    case 5 -> listarLibrosPorIdioma();
                    case 0 -> System.out.println("\n👋 ¡Gracias por usar LiterAlura! Cerrando aplicación...");
                    default -> System.out.println("\n❌ Opción no válida. Intente nuevamente.");
                }

            } catch (NumberFormatException e) {
                System.out.println("\n❌ Por favor, ingrese un número válido.");
            } catch (Exception e) {
                System.out.println("\n❌ Error: " + e.getMessage());
            }
        }

        scanner.close();
    }

    private void mostrarMenu() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("📚 LITERALURA - CATÁLOGO DE LIBROS");
        System.out.println("=".repeat(50));
        System.out.println("1 - Buscar libro por título");
        System.out.println("2 - Listar libros registrados");
        System.out.println("3 - Listar autores registrados");
        System.out.println("4 - Listar autores vivos en un determinado año");
        System.out.println("5 - Listar libros por idioma");
        System.out.println("0 - Salir");
        System.out.println("=".repeat(50));
        System.out.print("👉 Elija una opción: ");
    }

    private void buscarLibroPorTitulo() {
        System.out.println("\n📖 BUSCAR LIBRO");
        System.out.println("💡 Nota: La búsqueda es en el catálogo de Project Gutenberg.");
        System.out.println("   La mayoría de títulos están en inglés, aunque el libro tenga idioma español.");
        System.out.print("\nIngrese el título del libro (preferiblemente en inglés): ");
        String titulo = scanner.nextLine();

        if (titulo.isBlank()) {
            System.out.println("\n❌ El título no puede estar vacío.");
            return;
        }

        Libro libro = libroService.buscarYGuardarLibroPorTitulo(titulo);

        if (libro != null) {
            System.out.println("\n" + libro);
        }
    }

    private void listarLibrosRegistrados() {
        List<Libro> libros = libroService.listarTodosLosLibros();

        if (libros.isEmpty()) {
            System.out.println("\n⚠️ No hay libros registrados en la base de datos.");
            return;
        }

        System.out.println("\n" + "=".repeat(50));
        System.out.println("📚 LIBROS REGISTRADOS (" + libros.size() + ")");
        System.out.println("=".repeat(50));

        libros.forEach(System.out::println);
    }

    private void listarAutoresRegistrados() {
        List<Autor> autores = autorService.listarTodosLosAutores();

        if (autores.isEmpty()) {
            System.out.println("\n⚠️ No hay autores registrados en la base de datos.");
            return;
        }

        System.out.println("\n" + "=".repeat(50));
        System.out.println("✍️ AUTORES REGISTRADOS (" + autores.size() + ")");
        System.out.println("=".repeat(50));

        autores.forEach(autor -> {
            System.out.println("\n" + autor);
            System.out.println("-".repeat(50));
        });
    }

    private void listarAutoresVivosPorAnio() {
        System.out.print("\n📅 Ingrese el año para buscar autores vivos: ");

        try {
            Integer anio = Integer.parseInt(scanner.nextLine());

            List<Autor> autores = autorService.listarAutoresVivosEnAnio(anio);

            if (autores.isEmpty()) {
                return;
            }

            System.out.println("\n" + "=".repeat(50));
            System.out.println("✍️ AUTORES VIVOS EN EL AÑO " + anio + " (" + autores.size() + ")");
            System.out.println("=".repeat(50));

            autores.forEach(autor -> {
                System.out.println("\n" + autor);
                System.out.println("-".repeat(50));
            });

        } catch (NumberFormatException e) {
            System.out.println("\n❌ Por favor, ingrese un año válido.");
        }
    }

    private void listarLibrosPorIdioma() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("🌍 IDIOMAS DISPONIBLES:");
        System.out.println("=".repeat(50));
        System.out.println("es - Español");
        System.out.println("en - Inglés");
        System.out.println("fr - Francés");
        System.out.println("pt - Portugués");
        System.out.println("=".repeat(50));
        System.out.print("👉 Ingrese el código del idioma: ");

        String idioma = scanner.nextLine().toLowerCase().trim();

        if (idioma.isBlank()) {
            System.out.println("\n❌ El código de idioma no puede estar vacío.");
            return;
        }

        List<Libro> libros = libroService.listarLibrosPorIdioma(idioma);

        if (libros.isEmpty()) {
            return;
        }

        System.out.println("\n" + "=".repeat(50));
        System.out.println("📚 LIBROS EN IDIOMA: " + idioma.toUpperCase() + " (" + libros.size() + ")");
        System.out.println("=".repeat(50));

        libros.forEach(System.out::println);
    }
}
