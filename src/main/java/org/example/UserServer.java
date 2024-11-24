package org.example;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class UserServer {
    private static final String FILE_NAME = "users.txt";
    private static final Map<String, User> userDatabase = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        int port = 1234;

        // Load users from file
        loadUsersFromFile();

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is running on port " + port);

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New client connected");
                new ClientHandler(socket).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class ClientHandler extends Thread {
        private final Socket socket;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (
                    InputStream input = socket.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    OutputStream output = socket.getOutputStream();
                    PrintWriter writer = new PrintWriter(output, true)
            ) {
                String action;
                while ((action = reader.readLine()) != null) {
                    switch (action.toUpperCase()) {
                        case "GET":
                            String id = reader.readLine();
                            writer.println(get(id));
                            break;
                        case "UPDATE":
                            id = reader.readLine();
                            String name = reader.readLine();
                            String email = reader.readLine();
                            writer.println(update(id, name, email));
                            break;
                        case "GETALL":
                            writer.println(getAll());
                            break;
                        default:
                            writer.println("Invalid action");
                            break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private String getAll() {
            return new ArrayList<>(userDatabase.values()).toString();
        }

        private String get(String id) {
            User user = userDatabase.get(id);
            return user != null ? user.toString() : "User not found";
        }

        private String update(String id, String name, String email) {
            User user = userDatabase.get(id);
            if (user != null) {
                user.setName(name);
                user.setEmail(email);
                saveUsersToFile();
                return "User updated: " + user;
            }
            return "User not found";
        }
    }

    static class User {
        private String id;
        private String name;
        private String email;

        public User(String id, String name, String email) {
            this.id = id;
            this.name = name;
            this.email = email;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        @Override
        public String toString() {
            return "User{" + "id='" + id + '\'' + ", name='" + name + '\'' + ", email='" + email + '\'' + '}';
        }
    }

    private static void loadUsersFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    userDatabase.put(parts[0], new User(parts[0], parts[1], parts[2]));
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading users file: " + e.getMessage());
        }
    }

    private static void saveUsersToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_NAME))) {
            for (User user : userDatabase.values()) {
                writer.println(user.getId() + "," + user.getName() + "," + user.getEmail());
            }
        } catch (IOException e) {
            System.out.println("Error writing users file: " + e.getMessage());
        }
    }
}