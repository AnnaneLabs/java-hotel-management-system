package com.hotel.repository.jdbc;

import com.hotel.db.DatabaseConnection;
import com.hotel.model.Reservation;
import com.hotel.model.enums.ReservationStatus;
import com.hotel.repository.ReservationRepository;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ReservationRepositoryImpl implements ReservationRepository {

    private final Connection connection;

    public ReservationRepositoryImpl() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public Reservation save(Reservation reservation) {
        String sql = "INSERT INTO reservations (id, reservation_code, user_id, room_number, check_in, check_out, " +
                "number_of_guests, number_of_nights, total_price, status, created_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, reservation.getId());
            stmt.setString(2, reservation.getReservationCode());
            stmt.setObject(3, reservation.getUserId());
            stmt.setString(4, reservation.getRoomNumber());
            stmt.setDate(5, Date.valueOf(reservation.getCheckIn()));
            stmt.setDate(6, Date.valueOf(reservation.getCheckOut()));
            stmt.setInt(7, reservation.getNumberOfGuests());
            stmt.setLong(8, reservation.getNumberOfNights());
            stmt.setBigDecimal(9, reservation.getTotalPrice());
            stmt.setString(10, reservation.getStatus().name());
            stmt.setTimestamp(11, Timestamp.valueOf(reservation.getCreatedAt()));

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error saving reservation: " + e.getMessage(), e);
        }

        return reservation;
    }

    @Override
    public Optional<Reservation> findById(UUID id) {
        String sql = "SELECT * FROM reservations WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, id);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapRow(rs));
            } else {
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error finding reservation by id: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Reservation> findAll() {
        String sql = "SELECT * FROM reservations";
        List<Reservation> reservations = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                reservations.add(mapRow(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error finding all reservations: " + e.getMessage(), e);
        }

        return reservations;
    }

    @Override
    public List<Reservation> findByUserId(UUID userId) {
        String sql = "SELECT * FROM reservations WHERE user_id = ?";
        List<Reservation> reservations = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, userId);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                reservations.add(mapRow(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error finding reservations by user: " + e.getMessage(), e);
        }

        return reservations;
    }

    @Override
    public List<Reservation> findByRoomNumber(String roomNumber) {
        String sql = "SELECT * FROM reservations WHERE room_number = ?";
        List<Reservation> reservations = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, roomNumber);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                reservations.add(mapRow(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error finding reservations by room: " + e.getMessage(), e);
        }

        return reservations;
    }

    @Override
    public boolean existsOverlappingReservation(String roomNumber, LocalDate checkIn, LocalDate checkOut) {
        String sql = "SELECT COUNT(*) FROM reservations " +
                "WHERE room_number = ? " +
                "AND status != 'CANCELLED' " +
                "AND check_in < ? " +
                "AND check_out > ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, roomNumber);
            stmt.setDate(2, Date.valueOf(checkOut));
            stmt.setDate(3, Date.valueOf(checkIn));

            ResultSet rs = stmt.executeQuery();
            rs.next();
            int count = rs.getInt(1);

            return count > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error checking reservation overlap: " + e.getMessage(), e);
        }
    }

    @Override
    public void update(Reservation reservation) {
        String sql = "UPDATE reservations SET status = ?, total_price = ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, reservation.getStatus().name());
            stmt.setBigDecimal(2, reservation.getTotalPrice());
            stmt.setObject(3, reservation.getId());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error updating reservation: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(UUID id) {
        String sql = "DELETE FROM reservations WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, id);

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error deleting reservation: " + e.getMessage(), e);
        }
    }

    // Shared row-to-object mapping, used by every method that reads reservation rows back.
    private Reservation mapRow(ResultSet rs) throws SQLException {
        return new Reservation(
                (UUID) rs.getObject("id"),
                rs.getString("reservation_code"),
                (UUID) rs.getObject("user_id"),
                rs.getString("room_number"),
                rs.getDate("check_in").toLocalDate(),
                rs.getDate("check_out").toLocalDate(),
                rs.getInt("number_of_guests"),
                rs.getLong("number_of_nights"),
                rs.getBigDecimal("total_price"),
                ReservationStatus.valueOf(rs.getString("status")),
                rs.getTimestamp("created_at").toLocalDateTime()
        );
    }
}