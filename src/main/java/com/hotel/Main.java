package com.hotel;

import com.hotel.exception.RoomNotAvailableException;
import com.hotel.model.Room;
import com.hotel.model.User;
import com.hotel.model.enums.RoomStatus;
import com.hotel.model.enums.RoomType;
import com.hotel.model.enums.UserRole;
import com.hotel.repository.RoomRepository;
import com.hotel.repository.UserRepository;
import com.hotel.repository.jdbc.RoomRepositoryImpl;
import com.hotel.repository.jdbc.UserRepositoryImpl;
import com.hotel.service.AuthService;
import com.hotel.service.RoomService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);

    private static final UserRepository userRepository = new UserRepositoryImpl();
    private static final RoomRepository roomRepository = new RoomRepositoryImpl();
    private static final AuthService authService = new AuthService(userRepository);
    private static final RoomService roomService = new RoomService(roomRepository);

    private static User currentUser = null;

    public static void main(String[] args) {
        System.out.println("=== Hotel Management System ===");

        while (currentUser == null) {
            showAuthMenu();
        }

        showMainMenu();
    }

    private static void showAuthMenu() {
        System.out.println("\n1. Register");
        System.out.println("2. Login");
        System.out.println("3. Exit");
        System.out.print("Choose an option: ");

        String choice = scanner.nextLine();

        switch (choice) {
            case "1" -> handleRegister();
            case "2" -> handleLogin();
            case "3" -> {
                System.out.println("Goodbye!");
                System.exit(0);
            }
            default -> System.out.println("Invalid option, try again.");
        }
    }

    private static void handleRegister() {
        System.out.print("Full name: ");
        String fullName = scanner.nextLine();

        System.out.print("Email: ");
        String email = scanner.nextLine();

        System.out.print("Phone: ");
        String phone = scanner.nextLine();

        System.out.print("Password: ");
        String password = scanner.nextLine();

        try {
            User user = authService.register(fullName, email, phone, password, UserRole.CLIENT);
            System.out.println("Registered successfully: " + user);
        } catch (IllegalArgumentException e) {
            System.out.println("Registration failed: " + e.getMessage());
        }
    }

    private static void handleLogin() {
        System.out.print("Email: ");
        String email = scanner.nextLine();

        System.out.print("Password: ");
        String password = scanner.nextLine();

        try {
            currentUser = authService.login(email, password);
            System.out.println("Welcome, " + currentUser.getFullName() + "!");
        } catch (IllegalArgumentException e) {
            System.out.println("Login failed: " + e.getMessage());
        }
    }

    private static void showMainMenu() {
        boolean running = true;

        while (running) {
            System.out.println("\n=== Main Menu (" + currentUser.getFullName() + " - " + currentUser.getRole() + ") ===");
            System.out.println("1. View all rooms");
            System.out.println("2. View available rooms");
            if (currentUser.getRole() == UserRole.ADMIN) {
                System.out.println("3. Add a room");
                System.out.println("4. Change room status");
            }
            System.out.println("0. Logout / Exit");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1" -> viewAllRooms();
                case "2" -> viewAvailableRooms();
                case "3" -> {
                    if (currentUser.getRole() == UserRole.ADMIN) addRoom();
                    else System.out.println("Invalid option, try again.");
                }
                case "4" -> {
                    if (currentUser.getRole() == UserRole.ADMIN) changeRoomStatus();
                    else System.out.println("Invalid option, try again.");
                }
                case "0" -> {
                    System.out.println("Goodbye!");
                    running = false;
                }
                default -> System.out.println("Invalid option, try again.");
            }
        }
    }

    private static void viewAllRooms() {
        List<Room> rooms = roomService.getAllRooms();
        if (rooms.isEmpty()) {
            System.out.println("No rooms found.");
        } else {
            rooms.forEach(System.out::println);
        }
    }

    private static void viewAvailableRooms() {
        List<Room> rooms = roomService.getAvailableRooms();
        if (rooms.isEmpty()) {
            System.out.println("No available rooms.");
        } else {
            rooms.forEach(System.out::println);
        }
    }

    private static void addRoom() {
        System.out.print("Room number: ");
        String roomNumber = scanner.nextLine();

        System.out.print("Type (SINGLE, DOUBLE, SUITE): ");
        RoomType type = RoomType.valueOf(scanner.nextLine().toUpperCase());

        System.out.print("Capacity: ");
        int capacity = Integer.parseInt(scanner.nextLine());

        System.out.print("Price per night: ");
        BigDecimal price = new BigDecimal(scanner.nextLine());

        try {
            Room room = roomService.addRoom(roomNumber, type, capacity, price);
            System.out.println("Room added: " + room);
        } catch (IllegalArgumentException e) {
            System.out.println("Failed to add room: " + e.getMessage());
        }
    }

    private static void changeRoomStatus() {
        System.out.print("Room number: ");
        String roomNumber = scanner.nextLine();

        System.out.print("New status (AVAILABLE, OCCUPIED, MAINTENANCE): ");
        RoomStatus status = RoomStatus.valueOf(scanner.nextLine().toUpperCase());

        try {
            roomService.changeRoomStatus(roomNumber, status);
            System.out.println("Room status updated.");
        } catch (RoomNotAvailableException e) {
            System.out.println("Failed: " + e.getMessage());
        }
    }
}