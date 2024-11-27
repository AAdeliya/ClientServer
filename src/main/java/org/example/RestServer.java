package org.example;
import static spark.Spark.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
public class RestServer {
    public static void main(String[] args) {
        // Define the port for the REST API server
        port(8080);

        // API Endpoint to process client requests
        get("/api/data", (req, res) -> {
            res.type("application/json");

            // Fetch input from client query parameter
            String clientQuery = req.queryParams("query");
            if (clientQuery == null || clientQuery.isEmpty()) {
                return "{\"error\": \"Missing 'query' parameter\"}";
            }

            // External API call (example: JSONPlaceholder)
            String apiUrl = "https://jsonplaceholder.typicode.com/posts/1";
            String apiResponse = fetchFromApi(apiUrl);

            // Return the response as JSON
            return String.format("{\"clientInput\": \"%s\", \"apiResponse\": \"%s\"}", clientQuery, apiResponse);
        });

        System.out.println("RESTful API server is running on port 8080...");
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
