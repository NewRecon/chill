package ru.ivamly.chill.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ru.ivamly.chill.dto.AuthRq;
import ru.ivamly.chill.dto.AuthRs;
import ru.ivamly.chill.service.AuthService;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public AuthRs login (@Valid @RequestBody AuthRq request) {
        return new AuthRs(
            authService.authenticate(request.username(), request.password())
        );
    }

    // TODO /refresh
}
