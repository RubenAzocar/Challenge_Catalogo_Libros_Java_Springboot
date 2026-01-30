package com.alura.literalura;

import com.alura.literalura.dto.DatosRespuesta;
import com.alura.literalura.service.ConsumoAPI;
import com.alura.literalura.service.ConvierteDatos;

public class DiagnosticoTest {

    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("🔍 DIAGNÓSTICO DE LITERALURA");
        System.out.println("=".repeat(60));

        // Test 1: Conexión a la API
        testConexionAPI();

        // Test 2: Conversión de datos
        testConversionDatos();

        // Test 3: Búsqueda específica
        testBusquedaEspecifica();

        System.out.println("\n" + "=".repeat(60));
        System.out.println("✅ DIAGNÓSTICO COMPLETADO");
        System.out.println("=".repeat(60));
    }

    private static void testConexionAPI() {
        System.out.println("\n📡 TEST 1: Probando conexión a la API Gutendex...");
        try {
            ConsumoAPI consumoAPI = new ConsumoAPI();
            String url = "https://gutendex.com/books/?search=Moby%20Dick";
            String json = consumoAPI.obtenerDatos(url);

            if (json != null && !json.isEmpty()) {
                System.out.println("✅ Conexión exitosa");
                System.out.println("📊 Primeros 200 caracteres de respuesta:");
                System.out.println(json.substring(0, Math.min(200, json.length())));
            } else {
                System.out.println("❌ La respuesta está vacía");
            }
        } catch (Exception e) {
            System.out.println("❌ Error en conexión: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void testConversionDatos() {
        System.out.println("\n🔄 TEST 2: Probando conversión de JSON...");
        try {
            ConsumoAPI consumoAPI = new ConsumoAPI();
            ConvierteDatos conversor = new ConvierteDatos();

            String json = consumoAPI.buscarLibroPorTitulo("Don Quijote");
            DatosRespuesta respuesta = conversor.obtenerDatos(json, DatosRespuesta.class);

            if (respuesta != null) {
                System.out.println("✅ Conversión exitosa");
                System.out.println("📚 Resultados encontrados: " +
                    (respuesta.resultados() != null ? respuesta.resultados().size() : 0));

                if (respuesta.resultados() != null && !respuesta.resultados().isEmpty()) {
                    var primerLibro = respuesta.resultados().get(0);
                    System.out.println("📖 Primer resultado:");
                    System.out.println("   Título: " + primerLibro.titulo());
                    System.out.println("   Autores: " +
                        (primerLibro.autores() != null ? primerLibro.autores().size() : 0));
                    System.out.println("   Idiomas: " + primerLibro.idiomas());
                    System.out.println("   Descargas: " + primerLibro.numeroDescargas());
                }
            } else {
                System.out.println("❌ La respuesta es null");
            }
        } catch (Exception e) {
            System.out.println("❌ Error en conversión: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void testBusquedaEspecifica() {
        System.out.println("\n🔍 TEST 3: Probando búsquedas específicas...");

        String[] titulos = {
            "Pride and Prejudice",
            "Alice in Wonderland",
            "Frankenstein",
            "El Quijote",
            "Título Inexistente XYZ123"
        };

        ConsumoAPI consumoAPI = new ConsumoAPI();
        ConvierteDatos conversor = new ConvierteDatos();

        for (String titulo : titulos) {
            try {
                System.out.println("\n  🔎 Buscando: " + titulo);
                String json = consumoAPI.buscarLibroPorTitulo(titulo);
                DatosRespuesta respuesta = conversor.obtenerDatos(json, DatosRespuesta.class);

                if (respuesta != null && respuesta.resultados() != null) {
                    System.out.println("  ✅ Resultados: " + respuesta.resultados().size());
                } else {
                    System.out.println("  ⚠️ Sin resultados");
                }
            } catch (Exception e) {
                System.out.println("  ❌ Error: " + e.getMessage());
            }
        }
    }
}
