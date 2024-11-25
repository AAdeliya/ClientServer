package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;

public class Server {
    public static void main(String[] args) {
        try {
            System.out.println("Waiting for clients...");
            ServerSocket ss = new ServerSocket(9806);
            Socket soc = ss.accept();
            System.out.println("Connection established");

            // Reading the input from the client
            BufferedReader in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
            String clientInput = in.readLine();
            System.out.println("Received from client: " + clientInput);

            // Making an API call
            String apiUrl = "https://jsonplaceholder.typicode.com/posts/1";
            String apiResponse = fetchFromApi(apiUrl);

            // Sending the response back to the client
            PrintWriter out = new PrintWriter(soc.getOutputStream(), true);
            out.println("Server says: " + apiResponse);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to fetch data from the API
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
                response.append("Error: ").append(responseCode).append(" -- ").append(connection.getResponseCode());
            }
        } catch (IOException e) {
            response.append("IOException: ").append(e.getMessage());
            e.printStackTrace();
        }
        return response.toString();
    }
}