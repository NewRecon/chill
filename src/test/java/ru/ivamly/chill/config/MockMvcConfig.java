package ru.ivamly.chill.config;

import java.util.Set;
import java.util.UUID;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.MockMvcBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import lombok.RequiredArgsConstructor;
import ru.ivamly.chill.constant.TestConstant;
import ru.ivamly.chill.entity.User;
import ru.ivamly.chill.entity.enums.Role;
import ru.ivamly.chill.security.JwtProvider;

@TestConfiguration
@RequiredArgsConstructor
public class MockMvcConfig {

    private final JwtProvider jwtProvider;

    @Bean
    public MockMvcBuilderCustomizer securityCustomizerWithAuth() {

        String token = jwtProvider.generateToken(buildAdminUser());

        return builder -> builder.defaultRequest(
            MockMvcRequestBuilders.get("/")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
        );
    }

    private User buildAdminUser() {
        User user = new User();
        user.setId(UUID.fromString(TestConstant.ADMIN_ID));
        user.setName("admin");
        user.setRoles(Set.of(Role.ROLE_MANAGER, Role.ROLE_ADMIN));
        return user;
    }
}
