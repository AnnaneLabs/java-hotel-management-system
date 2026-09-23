package com.hotel.repository;

import com.hotel.model.Room;
import com.hotel.model.enums.RoomStatus;

import java.util.List;
import java.util.Optional;

public interface RoomRepository {
    Room save(Room room);
    Optional<Room> findByRoomNumber(String roomNumber);
    List<Room> findAll();
    List<Room> findByStatus(RoomStatus status);
    void update(Room room);
    void delete(String roomNumber);
}