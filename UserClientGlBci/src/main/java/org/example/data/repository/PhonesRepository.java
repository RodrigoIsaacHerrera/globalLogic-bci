package org.example.data.repository;

import org.example.data.entity.Phone;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PhonesRepository extends JpaRepository<Phone, Long> {
        Optional<Phone> findByUserId(UUID userId);
}
