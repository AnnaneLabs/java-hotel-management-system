package com.hotel;

import java.sql.Connection;
import java.sql.DriverManager;

public class Main {
    public static void main(String[] args) {
        String url = "jdbc:postgresql://localhost:5432/hotel_management";
        String user = "postgres";
        String password = "password"; // whatever you set in docker-compose.yml

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            System.out.println("Connected to PostgreSQL successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}