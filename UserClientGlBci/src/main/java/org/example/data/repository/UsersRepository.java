package org.example.data.repository;

import org.example.data.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UsersRepository extends JpaRepository<User, UUID> {
    // Métodos automáticos: save, findById, findAll, deleteById, etc.
}
