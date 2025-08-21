package org.example.data.repository;

import org.example.data.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UsersRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmailContainingIgnoreCase(String email);
    Optional<User> findByIdAndEmail(UUID id, String email);
    boolean existsById(UUID id);
}
