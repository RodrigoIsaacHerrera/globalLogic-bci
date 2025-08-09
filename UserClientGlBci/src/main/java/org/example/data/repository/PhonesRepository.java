package org.example.data.repository;

import org.example.data.entity.Phone;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhonesRepository extends JpaRepository<Phone, Long> {

}
