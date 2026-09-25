package com.hotel.repository.jdbc;

import com.hotel.db.DatabaseConnection;
import com.hotel.model.Payment;
import com.hotel.model.enums.PaymentMethod;
import com.hotel.model.enums.PaymentStatus;
import com.hotel.repository.PaymentRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class PaymentRepositoryImpl implements PaymentRepository {

    private final Connection connection;

    public PaymentRepositoryImpl() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public Payment save(Payment payment) {
        String sql = "INSERT INTO payments (id, reservation_id, amount, payment_method, status, paid_at) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, payment.getId());
            stmt.setObject(2, payment.getReservationId());
            stmt.setBigDecimal(3, payment.getAmount());
            stmt.setString(4, payment.getPaymentMethod().name());
            stmt.setString(5, payment.getStatus().name());
            stmt.setTimestamp(6, Timestamp.valueOf(payment.getPaidAt()));

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error saving payment: " + e.getMessage(), e);
        }

        return payment;
    }

    @Override
    public Optional<Payment> findById(UUID id) {
        String sql = "SELECT * FROM payments WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, id);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapRow(rs));
            } else {
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error finding payment by id: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Payment> findByReservationId(UUID reservationId) {
        String sql = "SELECT * FROM payments WHERE reservation_id = ?";
        List<Payment> payments = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, reservationId);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                payments.add(mapRow(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error finding payments by reservation: " + e.getMessage(), e);
        }

        return payments;
    }

    @Override
    public List<Payment> findAll() {
        String sql = "SELECT * FROM payments";
        List<Payment> payments = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                payments.add(mapRow(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error finding all payments: " + e.getMessage(), e);
        }

        return payments;
    }

    @Override
    public void update(Payment payment) {
        String sql = "UPDATE payments SET status = ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, payment.getStatus().name());
            stmt.setObject(2, payment.getId());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error updating payment: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(UUID id) {
        String sql = "DELETE FROM payments WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, id);

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error deleting payment: " + e.getMessage(), e);
        }
    }

    private Payment mapRow(ResultSet rs) throws SQLException {
        return new Payment(
                (UUID) rs.getObject("id"),
                (UUID) rs.getObject("reservation_id"),
                rs.getBigDecimal("amount"),
                PaymentMethod.valueOf(rs.getString("payment_method")),
                PaymentStatus.valueOf(rs.getString("status")),
                rs.getTimestamp("paid_at").toLocalDateTime()
        );
    }
}