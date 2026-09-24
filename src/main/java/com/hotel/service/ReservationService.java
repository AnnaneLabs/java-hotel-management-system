package com.hotel.service;

import com.hotel.exception.InvalidReservationException;
import com.hotel.exception.RoomNotAvailableException;
import com.hotel.model.Reservation;
import com.hotel.model.Room;
import com.hotel.model.enums.ReservationStatus;
import com.hotel.repository.ReservationRepository;
import com.hotel.repository.RoomRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;

    public ReservationService(ReservationRepository reservationRepository, RoomRepository roomRepository) {
        this.reservationRepository = reservationRepository;
        this.roomRepository = roomRepository;
    }

    public Reservation createReservation(UUID userId, String roomNumber, LocalDate checkIn, LocalDate checkOut,
                                         int numberOfGuests) {
        if (checkIn == null || checkOut == null || !checkOut.isAfter(checkIn)) {
            throw new InvalidReservationException("Check-out date must be after check-in date");
        }
        if (checkIn.isBefore(LocalDate.now())) {
            throw new InvalidReservationException("Check-in date cannot be in the past");
        }

        Room room = roomRepository.findByRoomNumber(roomNumber)
                .orElseThrow(() -> new RoomNotAvailableException("Room does not exist: " + roomNumber));

        if (numberOfGuests <= 0 || numberOfGuests > room.getCapacity()) {
            throw new InvalidReservationException(
                    "Number of guests (" + numberOfGuests + ") exceeds room capacity (" + room.getCapacity() + ")");
        }

        boolean overlaps = reservationRepository.existsOverlappingReservation(roomNumber, checkIn, checkOut);
        if (overlaps) {
            throw new RoomNotAvailableException(
                    "Room " + roomNumber + " is already booked for part or all of that date range");
        }

        long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
        // Simple nightly-rate pricing for now — replaced by PricingStrategy (season/weekend/
        // long-stay/early/last-minute rules) once we build it on Day 6.
        BigDecimal totalPrice = room.getPricePerNight().multiply(BigDecimal.valueOf(nights));

        Reservation reservation = new Reservation(userId, roomNumber, checkIn, checkOut, numberOfGuests, totalPrice);
        return reservationRepository.save(reservation);
    }

    public Reservation getReservationById(UUID id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new InvalidReservationException("Reservation not found: " + id));
    }

    public List<Reservation> getReservationsForUser(UUID userId) {
        return reservationRepository.findByUserId(userId);
    }

    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    public void cancelReservation(UUID reservationId) {
        Reservation reservation = getReservationById(reservationId);

        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            throw new InvalidReservationException("Reservation is already cancelled");
        }
        if (reservation.getCheckIn().isBefore(LocalDate.now())) {
            throw new InvalidReservationException("Cannot cancel a reservation that has already started");
        }

        reservation.setStatus(ReservationStatus.CANCELLED);
        reservationRepository.update(reservation);
    }
}