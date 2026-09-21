package com.hotel.repository;

import com.hotel.model.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
   User save(User user);
//   Optional<User> findById(UUID id);
//   Optional<User> findByEmail(String email);
//   List<User> findAll();
//   void update(User user);
//   void delete(UUID id);
}