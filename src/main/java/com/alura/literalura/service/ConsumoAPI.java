package com.alura.literalura.service;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class ConsumoAPI {

    private static final String URL_BASE = "https://gutendex.com/books/";

    public String obtenerDatos(String url) {
        HttpClient client = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Accept", "application/json")
                .build();

        HttpResponse<String> response;

        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Error al consumir la API: " + e.getMessage());
        }
    }

    public String buscarLibroPorTitulo(String titulo) {
        // Codificar correctamente el título incluyendo caracteres especiales (tildes, ñ, etc.)
        String tituloFormateado = URLEncoder.encode(titulo, StandardCharsets.UTF_8);
        String url = URL_BASE + "?search=" + tituloFormateado;
        return obtenerDatos(url);
    }
}
