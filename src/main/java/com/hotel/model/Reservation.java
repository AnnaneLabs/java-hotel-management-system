package com.hotel.model;

import com.hotel.model.enums.ReservationStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

public class Reservation {

    private UUID id;
    private String reservationCode;
    private UUID userId;
    private String roomNumber;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private int numberOfGuests;
    private long numberOfNights;
    private BigDecimal totalPrice;
    private ReservationStatus status;
    private LocalDateTime createdAt;

    // Constructor for a brand-new reservation (id, code, nights, status, createdAt all derived here)
    public Reservation(UUID userId, String roomNumber, LocalDate checkIn, LocalDate checkOut,
                       int numberOfGuests, BigDecimal totalPrice) {
        this.id = UUID.randomUUID();
        this.reservationCode = "RES-" + this.id.toString().substring(0, 8).toUpperCase();
        this.userId = userId;
        this.roomNumber = roomNumber;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.numberOfGuests = numberOfGuests;
        this.numberOfNights = ChronoUnit.DAYS.between(checkIn, checkOut);
        this.totalPrice = totalPrice;
        this.status = ReservationStatus.CONFIRMED;
        this.createdAt = LocalDateTime.now();
    }

    // Constructor for reconstructing an existing reservation from the database
    public Reservation(UUID id, String reservationCode, UUID userId, String roomNumber,
                       LocalDate checkIn, LocalDate checkOut, int numberOfGuests, long numberOfNights,
                       BigDecimal totalPrice, ReservationStatus status, LocalDateTime createdAt) {
        this.id = id;
        this.reservationCode = reservationCode;
        this.userId = userId;
        this.roomNumber = roomNumber;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.numberOfGuests = numberOfGuests;
        this.numberOfNights = numberOfNights;
        this.totalPrice = totalPrice;
        this.status = status;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public String getReservationCode() {
        return reservationCode;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public LocalDate getCheckIn() {
        return checkIn;
    }

    public LocalDate getCheckOut() {
        return checkOut;
    }

    public int getNumberOfGuests() {
        return numberOfGuests;
    }

    public long getNumberOfNights() {
        return numberOfNights;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // Only status and totalPrice are mutable after creation.
    // id, reservationCode, userId, roomNumber, checkIn, checkOut, numberOfGuests, numberOfNights
    // are fixed at creation time on purpose (same immutability reasoning as User.id / Room.roomNumber).
    public void setStatus(ReservationStatus status) {
        this.status = status;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "id=" + id +
                ", reservationCode='" + reservationCode + '\'' +
                ", userId=" + userId +
                ", roomNumber='" + roomNumber + '\'' +
                ", checkIn=" + checkIn +
                ", checkOut=" + checkOut +
                ", numberOfGuests=" + numberOfGuests +
                ", numberOfNights=" + numberOfNights +
                ", totalPrice=" + totalPrice +
                ", status=" + status +
                '}';
    }
}