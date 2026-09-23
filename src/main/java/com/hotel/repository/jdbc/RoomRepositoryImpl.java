package com.hotel.repository.jdbc;

import com.hotel.db.DatabaseConnection;
import com.hotel.model.Room;
import com.hotel.model.enums.RoomStatus;
import com.hotel.model.enums.RoomType;
import com.hotel.repository.RoomRepository;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RoomRepositoryImpl implements RoomRepository {

    private final Connection connection;

    public RoomRepositoryImpl() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public Room save(Room room) {
        String sql = "INSERT INTO rooms (room_number, type, capacity, price_per_night, status) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, room.getRoomNumber());
            stmt.setString(2, room.getType().name());
            stmt.setInt(3, room.getCapacity());
            stmt.setBigDecimal(4, room.getPricePerNight());
            stmt.setString(5, room.getStatus().name());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error saving room: " + e.getMessage(), e);
        }

        return room;
    }

    @Override
    public Optional<Room> findByRoomNumber(String roomNumber) {
        String sql = "SELECT * FROM rooms WHERE room_number = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, roomNumber);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Room room = new Room(
                        rs.getString("room_number"),
                        RoomType.valueOf(rs.getString("type")),
                        rs.getInt("capacity"),
                        rs.getBigDecimal("price_per_night"),
                        RoomStatus.valueOf(rs.getString("status"))
                );
                return Optional.of(room);
            } else {
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error finding room by room number: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Room> findAll() {
        String sql = "SELECT * FROM rooms";
        List<Room> rooms = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Room room = new Room(
                        rs.getString("room_number"),
                        RoomType.valueOf(rs.getString("type")),
                        rs.getInt("capacity"),
                        rs.getBigDecimal("price_per_night"),
                        RoomStatus.valueOf(rs.getString("status"))
                );
                rooms.add(room);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error finding all rooms: " + e.getMessage(), e);
        }

        return rooms;
    }

    @Override
    public List<Room> findByStatus(RoomStatus status) {
        String sql = "SELECT * FROM rooms WHERE status = ?";
        List<Room> rooms = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, status.name());

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Room room = new Room(
                        rs.getString("room_number"),
                        RoomType.valueOf(rs.getString("type")),
                        rs.getInt("capacity"),
                        rs.getBigDecimal("price_per_night"),
                        RoomStatus.valueOf(rs.getString("status"))
                );
                rooms.add(room);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error finding rooms by status: " + e.getMessage(), e);
        }

        return rooms;
    }

    @Override
    public void update(Room room) {
        String sql = "UPDATE rooms SET type = ?, capacity = ?, price_per_night = ?, status = ? WHERE room_number = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, room.getType().name());
            stmt.setInt(2, room.getCapacity());
            stmt.setBigDecimal(3, room.getPricePerNight());
            stmt.setString(4, room.getStatus().name());
            stmt.setString(5, room.getRoomNumber());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error updating room: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(String roomNumber) {
        String sql = "DELETE FROM rooms WHERE room_number = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, roomNumber);

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error deleting room: " + e.getMessage(), e);
        }
    }
}