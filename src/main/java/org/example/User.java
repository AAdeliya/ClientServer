package org.example;

import java.util.ArrayList;
import java.util.List;

public class User {

        private String id;
        private String name;
        private String email;
        private List<String> courses;

        public User(String id, String name, String email) {
            this.id = id;
            this.name = name;
            this.email = email;
            this.courses = new ArrayList<>();
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

        public List<String> getCourses() {
            return courses;
        }

        @Override
        public String toString() {
            return "User{" +
                    "id='" + id + '\'' +
                    ", name='" + name + '\'' +
                    ", email='" + email + '\'' +
                    ", courses=" + courses +
                    '}';
        }
    }

