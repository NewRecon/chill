package ru.ivamly.chill.service;

import java.util.HashSet;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import ru.ivamly.chill.entity.User;
import ru.ivamly.chill.exception.UnauthorizedException;
import ru.ivamly.chill.security.JwtProvider;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtProvider jwtProvider;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;

    public String authenticate(String username, String password) {

        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
            );
        } catch (AuthenticationException e) {
            throw new UnauthorizedException("Неверный логин или пароль");
        }

        if (!authentication.isAuthenticated()) {
            throw new UnauthorizedException("Непредвиденная ошибка при аутентификации пользователя");
        }

        User user = userService.findByName(username)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setName(username);
                    newUser.setRoles(new HashSet<>());
                    return userService.create(newUser);
                });

        return jwtProvider.generateToken(user);
    }
}
