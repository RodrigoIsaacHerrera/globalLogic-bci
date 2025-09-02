package org.example.data.repository;

import org.example.data.entity.UserCustom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UsersRepository extends JpaRepository<UserCustom, UUID> {

    Optional<UserCustom> findByEmailContainingIgnoreCase(String email);
    Optional<UserCustom> findByIdAndEmail(UUID id, String email);
    boolean existsById(UUID id);
}
