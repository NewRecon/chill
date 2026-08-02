package ru.ivamly.chill.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.ivamly.chill.entity.User;

public interface UserRepository extends JpaRepository<User, UUID> {
    
    boolean existsByName(String name);

    Optional<User> findByName(String name);
}
