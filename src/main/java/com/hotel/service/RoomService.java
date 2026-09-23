package com.hotel.service;

import com.hotel.exception.RoomNotAvailableException;
import com.hotel.model.Room;
import com.hotel.model.enums.RoomStatus;
import com.hotel.model.enums.RoomType;
import com.hotel.repository.RoomRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class RoomService {

    private final RoomRepository roomRepository;

    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    public Room addRoom(String roomNumber, RoomType type, int capacity, BigDecimal pricePerNight) {
        Optional<Room> existing = roomRepository.findByRoomNumber(roomNumber);
        if (existing.isPresent()) {
            throw new IllegalArgumentException("Room number already exists: " + roomNumber);
        }

        Room room = new Room(roomNumber, type, capacity, pricePerNight, RoomStatus.AVAILABLE);
        return roomRepository.save(room);
    }

    public Room getRoomByNumber(String roomNumber) {
        return roomRepository.findByRoomNumber(roomNumber)
                .orElseThrow(() -> new RoomNotAvailableException("Room not found: " + roomNumber));
    }

    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    public List<Room> getAvailableRooms() {
        return roomRepository.findByStatus(RoomStatus.AVAILABLE);
    }

    public void changeRoomStatus(String roomNumber, RoomStatus newStatus) {
        Room room = getRoomByNumber(roomNumber);
        room.setStatus(newStatus);
        roomRepository.update(room);
    }

    public void removeRoom(String roomNumber) {
        getRoomByNumber(roomNumber);
        roomRepository.delete(roomNumber);
    }
}