package com.hotel;

import com.hotel.exception.InvalidReservationException;
import com.hotel.exception.RoomNotAvailableException;
import com.hotel.model.Reservation;
import com.hotel.model.Room;
import com.hotel.model.User;
import com.hotel.model.enums.RoomStatus;
import com.hotel.model.enums.RoomType;
import com.hotel.model.enums.UserRole;
import com.hotel.repository.ReservationRepository;
import com.hotel.repository.RoomRepository;
import com.hotel.repository.UserRepository;
import com.hotel.repository.jdbc.ReservationRepositoryImpl;
import com.hotel.repository.jdbc.RoomRepositoryImpl;
import com.hotel.repository.jdbc.UserRepositoryImpl;
import com.hotel.service.AuthService;
import com.hotel.service.ReservationService;
import com.hotel.service.RoomService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);

    private static final UserRepository userRepository = new UserRepositoryImpl();
    private static final RoomRepository roomRepository = new RoomRepositoryImpl();
    private static final ReservationRepository reservationRepository = new ReservationRepositoryImpl();

    private static final AuthService authService = new AuthService(userRepository);
    private static final RoomService roomService = new RoomService(roomRepository);
    private static final ReservationService reservationService =
            new ReservationService(reservationRepository, roomRepository);

    private static User currentUser = null;

    public static void main(String[] args) {
        System.out.println("=== Hotel Management System ===");

        while (currentUser == null) {
            showAuthMenu();
        }

        showMainMenu();
    }

    // ================= AUTH =================

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

    // ================= MAIN MENU =================

    private static void showMainMenu() {
        boolean running = true;

        while (running) {
            boolean isAdmin = currentUser.getRole() == UserRole.ADMIN;

            System.out.println("\n=== Main Menu (" + currentUser.getFullName() + " - " + currentUser.getRole() + ") ===");
            System.out.println("1. View all rooms");
            System.out.println("2. View available rooms");
            System.out.println("3. Book a room");
            System.out.println("4. View my reservations");
            System.out.println("5. Cancel a reservation");
            if (isAdmin) {
                System.out.println("6. Add a room");
                System.out.println("7. Change room status");
                System.out.println("8. View all reservations");
            }
            System.out.println("0. Logout / Exit");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1" -> viewAllRooms();
                case "2" -> viewAvailableRooms();
                case "3" -> bookRoom();
                case "4" -> viewMyReservations();
                case "5" -> cancelReservation();
                case "6" -> { if (isAdmin) addRoom(); else invalid(); }
                case "7" -> { if (isAdmin) changeRoomStatus(); else invalid(); }
                case "8" -> { if (isAdmin) viewAllReservations(); else invalid(); }
                case "0" -> {
                    System.out.println("Goodbye!");
                    running = false;
                }
                default -> invalid();
            }
        }
    }

    private static void invalid() {
        System.out.println("Invalid option, try again.");
    }

    // ================= ROOMS =================

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
        RoomType type;
        try {
            type = RoomType.valueOf(scanner.nextLine().toUpperCase());
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid room type.");
            return;
        }

        System.out.print("Capacity: ");
        int capacity;
        try {
            capacity = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid capacity.");
            return;
        }

        System.out.print("Price per night: ");
        BigDecimal price;
        try {
            price = new BigDecimal(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid price.");
            return;
        }

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
        RoomStatus status;
        try {
            status = RoomStatus.valueOf(scanner.nextLine().toUpperCase());
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid status.");
            return;
        }

        try {
            roomService.changeRoomStatus(roomNumber, status);
            System.out.println("Room status updated.");
        } catch (RoomNotAvailableException e) {
            System.out.println("Failed: " + e.getMessage());
        }
    }

    // ================= RESERVATIONS =================

    private static void bookRoom() {
        System.out.print("Room number: ");
        String roomNumber = scanner.nextLine();

        LocalDate checkIn = readDate("Check-in date (yyyy-MM-dd): ");
        if (checkIn == null) return;

        LocalDate checkOut = readDate("Check-out date (yyyy-MM-dd): ");
        if (checkOut == null) return;

        System.out.print("Number of guests: ");
        int guests;
        try {
            guests = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid number of guests.");
            return;
        }

        try {
            Reservation reservation = reservationService.createReservation(
                    currentUser.getId(), roomNumber, checkIn, checkOut, guests);
            System.out.println("Reservation confirmed: " + reservation);
        } catch (InvalidReservationException | RoomNotAvailableException e) {
            System.out.println("Booking failed: " + e.getMessage());
        }
    }

    private static void viewMyReservations() {
        List<Reservation> reservations = reservationService.getReservationsForUser(currentUser.getId());
        if (reservations.isEmpty()) {
            System.out.println("You have no reservations.");
        } else {
            reservations.forEach(System.out::println);
        }
    }

    private static void viewAllReservations() {
        List<Reservation> reservations = reservationService.getAllReservations();
        if (reservations.isEmpty()) {
            System.out.println("No reservations found.");
        } else {
            reservations.forEach(System.out::println);
        }
    }

    private static void cancelReservation() {
        System.out.print("Reservation id to cancel: ");
        String rawId = scanner.nextLine();

        UUID id;
        try {
            id = UUID.fromString(rawId);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid reservation id format.");
            return;
        }

        try {
            reservationService.cancelReservation(id);
            System.out.println("Reservation cancelled.");
        } catch (InvalidReservationException e) {
            System.out.println("Cancellation failed: " + e.getMessage());
        }
    }

    // ================= HELPERS =================

    private static LocalDate readDate(String prompt) {
        System.out.print(prompt);
        String raw = scanner.nextLine();
        try {
            return LocalDate.parse(raw);
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format, expected yyyy-MM-dd.");
            return null;
        }
    }
}