package com.hotel.repository;

import com.hotel.model.Payment;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository {
    Payment save(Payment payment);
    Optional<Payment> findById(UUID id);
    List<Payment> findByReservationId(UUID reservationId);
    List<Payment> findAll();
    void update(Payment payment);
    void delete(UUID id);
}