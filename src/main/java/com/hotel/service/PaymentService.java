package com.hotel.service;

import com.hotel.exception.InvalidReservationException;
import com.hotel.model.Payment;
import com.hotel.model.Reservation;
import com.hotel.model.enums.PaymentMethod;
import com.hotel.model.enums.PaymentStatus;
import com.hotel.repository.PaymentRepository;
import com.hotel.repository.ReservationRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;

    public PaymentService(PaymentRepository paymentRepository, ReservationRepository reservationRepository) {
        this.paymentRepository = paymentRepository;
        this.reservationRepository = reservationRepository;
    }

    public Payment recordPayment(UUID reservationId, BigDecimal amount, PaymentMethod method) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new InvalidReservationException("Reservation not found: " + reservationId));

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidReservationException("Payment amount must be greater than zero");
        }

        if (amount.compareTo(reservation.getTotalPrice()) != 0) {
            throw new InvalidReservationException(
                    "Payment amount (" + amount + ") does not match reservation total (" + reservation.getTotalPrice() + ")");
        }

        Payment payment = new Payment(reservationId, amount, method);
        payment.setStatus(PaymentStatus.PAID);

        return paymentRepository.save(payment);
    }

    public List<Payment> getPaymentsForReservation(UUID reservationId) {
        return paymentRepository.findByReservationId(reservationId);
    }

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    public void markAsRefunded(UUID paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new InvalidReservationException("Payment not found: " + paymentId));

        payment.setStatus(PaymentStatus.REFUNDED);
        paymentRepository.update(payment);
    }
}