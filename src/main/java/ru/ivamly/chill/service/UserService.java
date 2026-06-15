package ru.ivamly.chill.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

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

    public User create(User user) {
        if (userRepository.existsByName(user.getName())) {
            throw new UserExistException(user.getName());
        }
        return userRepository.save(user);
    }

    public Optional<User> findByName(String name) {
        return userRepository.findByName(name);
    }

    public User getById(UUID id) throws EntityNotFoundException {
        return userRepository.findById(id)
            .orElseThrow(EntityNotFoundException::new);
    }

    public void assignRole(UUID id, Role role) {
        User user = getById(id);
        user.getRoles().add(role);

        userRepository.save(user);
    }

    public void revokeRole(UUID id, Role role) {
        User user = getById(id);
        user.getRoles().remove(role);

        userRepository.save(user);
    }
}
