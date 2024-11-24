package org.example;

import java.io.*;
import java.net.*;

public class UserClient {
    public static void main(String[] args) {
        String hostname = "localhost";
        int port = 1234;

        try (
                Socket socket = new Socket(hostname, port);
                OutputStream output = socket.getOutputStream();
                PrintWriter writer = new PrintWriter(output, true);
                InputStream input = socket.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in))
        ) {
            String command;
            while (true) {
                System.out.println("Enter command (GET, UPDATE, GETALL, EXIT):");
                command = consoleReader.readLine();

                if ("EXIT".equalsIgnoreCase(command)) break;

                writer.println(command);

                switch (command.toUpperCase()) {
                    case "GET":
                        System.out.print("Enter user ID: ");
                        writer.println(consoleReader.readLine());
                        System.out.println(reader.readLine());
                        break;

                    case "UPDATE":
                        System.out.print("Enter user ID: ");
                        writer.println(consoleReader.readLine());
                        System.out.print("Enter new name: ");
                        writer.println(consoleReader.readLine());
                        System.out.print("Enter new email: ");
                        writer.println(consoleReader.readLine());
                        System.out.println(reader.readLine());
                        break;

                    case "GETALL":
                        System.out.println(reader.readLine());
                        break;

                    default:
                        System.out.println("Invalid command");
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}