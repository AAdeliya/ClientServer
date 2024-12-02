package org.example;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;

public class RestServer {

    public static void main(String[] args) {
        try {
            // Create an HTTP server that listens on port 8080
            HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

            // Define a context (route) for the API
            server.createContext("/api/data", new MyHandler());

            // Start the server
            server.setExecutor(null); // Default executor
            server.start();
            System.out.println("Server is running on http://localhost:8080/api/data");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Define the handler for processing HTTP requests
    static class MyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) {
            try {
                // Only allow GET requests
                if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                    // Parse query parameters
                    String query = exchange.getRequestURI().getQuery();
                    String clientQuery = null;
                    if (query != null && query.startsWith("query=")) {
                        clientQuery = query.substring("query=".length());
                    }

                    if (clientQuery == null || clientQuery.isEmpty()) {
                        sendResponse(exchange, 400, "{\"error\": \"Missing 'query' parameter\"}");
                        return;
                    }

                    // Call an external API
                    String apiUrl = "https://jsonplaceholder.typicode.com/posts/1";
                    String apiResponse = fetchFromApi(apiUrl);

                    // Build the JSON response
                    String jsonResponse = String.format("{\"clientInput\": \"%s\", \"apiResponse\": \"%s\"}", clientQuery, apiResponse);
                    sendResponse(exchange, 200, jsonResponse);

                } else {
                    // Return 405 Method Not Allowed for non-GET requests
                    sendResponse(exchange, 405, "{\"error\": \"Method not allowed\"}");
                }

            } catch (Exception e) {
                e.printStackTrace();
                sendResponse(exchange, 500, "{\"error\": \"Internal server error\"}");
            }
        }
    }

    // Method to send an HTTP response
    private static void sendResponse(HttpExchange exchange, int statusCode, String response) {
        try {
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(statusCode, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to fetch data from an external API
    public static String fetchFromApi(String apiUrl) {
        StringBuilder response = new StringBuilder();
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000); // 5 seconds timeout
            connection.setReadTimeout(5000);

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
                in.close();
            } else {
                response.append("Error: ").append(responseCode).append(" - ").append(connection.getResponseMessage());
            }
        } catch (Exception e) {
            response.append("Exception: ").append(e.getMessage());
            e.printStackTrace();
        }
        return response.toString();
    }
}