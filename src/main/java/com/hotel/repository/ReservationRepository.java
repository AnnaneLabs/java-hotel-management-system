package com.hotel.repository;

import com.hotel.model.Reservation;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReservationRepository {
    Reservation save(Reservation reservation);
    Optional<Reservation> findById(UUID id);
    List<Reservation> findAll();
    List<Reservation> findByUserId(UUID userId);
    List<Reservation> findByRoomNumber(String roomNumber);
    boolean existsOverlappingReservation(String roomNumber, LocalDate checkIn, LocalDate checkOut);
    void update(Reservation reservation);
    void delete(UUID id);
}