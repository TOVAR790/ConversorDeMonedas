package org.conversor;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.net.http.HttpHeaders;

public class OpenExchangeRates {

    private static final String API_KEY = "d8a213fe9e9a47e9a5f6ec2d373d79d2";
    private static final String API_URL = "https://openexchangerates.org/account/app-ids" + API_KEY;


    public static void main(String[] args) {
        // Crear un cliente HttpClient con redirección automática habilitada
        HttpClient client = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();

        // Crear una solicitud HttpRequest
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .build();

        // Enviar la solicitud y manejar la respuesta de forma asíncrona
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(OpenExchangeRates ::handleResponse)
                .exceptionally(ex -> {
                    System.out.println("Error durante la solicitud: " + ex.getMessage());
                    return null;
                })
                .join();
    }

    private static String handleResponse(HttpResponse<String> response) {
        // Extraer el código de estado de la respuesta
        int statusCode = response.statusCode();
        System.out.println("Status Code: " + statusCode);

        // Extraer los encabezados de la respuesta
        HttpHeaders headers = response.headers();
        headers.map().forEach((k, v) -> System.out.println(k + ":" + v));

        // Extraer el cuerpo de la respuesta
        String responseBody = response.body();
        System.out.println("Response Body: " + responseBody);

        // Verificar si el contenido es JSON y manejar la respuesta
        if (isJsonResponse(responseBody)) {
            try {
                // Parsear el cuerpo de la respuesta si es JSON
                JsonObject jsonObject = JsonParser.parseString(responseBody).getAsJsonObject();
                JsonObject rates = jsonObject.getAsJsonObject("conversion_rates");

                // Ejemplo: Convertir de USD a EUR
                double usdToEur = rates.get("EUR").getAsDouble();
                System.out.println("USD to EUR rate: " + usdToEur);
            } catch (Exception e) {
                System.out.println("Error al parsear JSON: " + e.getMessage());
            }
        } else {
            System.out.println("La respuesta no es JSON.");
        }

        return responseBody;
    }

    private static boolean isJsonResponse(String responseBody) {
        // Verificar si la respuesta comienza con '{' indicando JSON
        return responseBody.trim().startsWith("{");

    }
}
