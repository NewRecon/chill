package ru.ivamly.chill.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import ru.ivamly.chill.entity.User;
import ru.ivamly.chill.entity.enums.Role;
import ru.ivamly.chill.exception.UserExistException;
import ru.ivamly.chill.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public User create(User user) {
        if (userRepository.existsByName(user.getName())) {
            throw new UserExistException(user.getName());
        }
        return userRepository.save(user);
    }

    public Optional<User> findByName(String name) {
        return userRepository.findByName(name);
    }

    public User getById(UUID id) {
        return userRepository.findById(id)
            .orElseThrow(EntityNotFoundException::new);
    }

    @Transactional
    public void assignRole(UUID id, Role role) {
        User user = getById(id);
        user.getRoles().add(role);
    }

    @Transactional
    public void revokeRole(UUID id, Role role) {
        User user = getById(id);
        user.getRoles().remove(role);
    }
}
