package ru.ivamly.chill.it;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashSet;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.ResultActions;

import jakarta.servlet.http.HttpSession;
import lombok.SneakyThrows;
import ru.ivamly.chill.dto.AuthRq;
import ru.ivamly.chill.dto.UpdateRoleRq;
import ru.ivamly.chill.entity.User;
import ru.ivamly.chill.entity.enums.Role;
import ru.ivamly.chill.security.JwtProvider;

@DisplayName("Аутентифицировать и авторизовать пользователя")
public class SecurityTest extends BaseIntegrationTest {

    @Autowired
    private JwtProvider jwtProvider; 

    @Test
    @SneakyThrows
    @DisplayName("Успешная аутентификация без создания сессии")
    void shouldAuthenticateWithoutSession() {

        // given
        var testAuthority = getContent(new AuthRq("user", "user"));

        // when
        ResultActions perform = mockMvc.perform(post("/api/1/auth/login")
                .with(request -> {
                        request.removeHeader(HttpHeaders.AUTHORIZATION);
                        return request;
                    }
                )
                .content(testAuthority));
            
        // then
        perform.andExpect(status().is2xxSuccessful());

        HttpSession session = perform.andReturn().getRequest().getSession(false);
        assertNull(session); 
    }

    @Test
    @SneakyThrows
    @DisplayName("Ошибка при попытке вызвать апи без необходимых прав")
    void shouldReturnForbidden() {

        // given
        var userId = UUID.randomUUID();
        var testAuthority = getContent(new UpdateRoleRq(userId, Role.ROLE_MANAGER));
        var user = new User();
        user.setId(userId);
        user.setName("user");
        user.setRoles(new HashSet<>());
        var token = jwtProvider.generateToken(user);

        // when
        ResultActions perform = mockMvc.perform(post("/api/1/users/assignRole")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .content(testAuthority));

        // then
        perform.andExpect(status().isForbidden());
    }

    @Test
    @SneakyThrows
    @DisplayName("Ошибка при попытке вызвать апи без аутентификации")
    void shouldReturnUnauthorized() {

        // given
        var testAuthority = getContent(Role.ROLE_MANAGER);

        // when
        ResultActions perform = mockMvc.perform(post("/api/1/users/assignRole")
                .with(request -> {
                        request.removeHeader(HttpHeaders.AUTHORIZATION);
                        return request;
                    }
                )
                .content(testAuthority));
            
        // then
        perform.andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @DisplayName("Ошибка при попытке аутентификации с некорректным паролем")
    void shouldReturnUnauthorizedWhithBadCredetials() {

        // given
        var testAuthority = getContent(new AuthRq("user", "uncorrect_password"));

        // when
        ResultActions perform = mockMvc.perform(post("/api/1/auth/login")
                .with(request -> {
                        request.removeHeader(HttpHeaders.AUTHORIZATION);
                        return request;
                    }
                )
                .content(testAuthority));
            
        // then
        perform.andExpect(status().isUnauthorized());
    }
}
