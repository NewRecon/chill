package ru.ivamly.chill.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import ru.ivamly.chill.entity.User;

public interface UserRepository extends JpaRepository<User, UUID> {
    
    @Query("""
            SELECT EXISTS (
            SELECT 1 FROM User u
            WHERE u.name = :name
            )
            """)
    boolean existsByName(String name);

    Optional<User> findByName(String name);
}
