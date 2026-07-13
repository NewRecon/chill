package ru.ivamly.chill.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ru.ivamly.chill.dto.GetChillsRs;
import ru.ivamly.chill.dto.UpdateRoleRq;
import ru.ivamly.chill.mapper.ChillMapper;
import ru.ivamly.chill.service.ChillService;
import ru.ivamly.chill.service.UserService;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController { // TODO добавить сваггер

    private final ChillService chillService;
    private final ChillMapper chillMapper;
    private final UserService userService;

    @GetMapping("/{id}/chills")
    public GetChillsRs get(@PathVariable UUID id) {
        return new GetChillsRs(
                chillMapper.map(
                        chillService.findByUserId(id)
                )
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/assignRole")
    public ResponseEntity<Void> assignRole(@Valid @RequestBody UpdateRoleRq request) {
        userService.assignRole(request.userId(), request.role());
        
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/revokeRole")
    public ResponseEntity<Void> revokeRole(@Valid @RequestBody UpdateRoleRq request) {
        userService.revokeRole(request.userId(), request.role());
        
        return ResponseEntity.ok().build();
    }
}
