package com.hotel;

import com.hotel.db.DatabaseConnection;
import com.hotel.model.User;
import com.hotel.model.enums.UserRole;
import com.hotel.repository.UserRepository;
import com.hotel.repository.jdbc.UserRepositoryImpl;

public class Main {
    public static void main(String[] args) {
        UserRepository repo = new UserRepositoryImpl();
        User user = new User("Hamza Annane","annane@gmail.com","+212678789076","password123","xyz", UserRole.CLIENT);
        repo.save(user);
        System.out.println("Saved: " + user);
    }
}