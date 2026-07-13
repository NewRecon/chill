package ru.ivamly.chill.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.support.AbstractContextSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.ldap.authentication.BindAuthenticator;
import org.springframework.security.ldap.authentication.LdapAuthenticationProvider;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import ru.ivamly.chill.security.DatabaseLdapAuthoritiesPopulator;
import ru.ivamly.chill.security.JwtAuthenticationFilter;
import ru.ivamly.chill.security.JwtProvider;
import tools.jackson.databind.ObjectMapper;

@EnableMethodSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final DatabaseLdapAuthoritiesPopulator databaseLdapAuthoritiesPopulator;
    private final AbstractContextSource contextSource;
    private final JwtProvider jwtProvider;

    @Value("${ldap.userDnPattern}")
    private String userDnPattern;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, ObjectMapper objectMapper) {
        return http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(new JwtAuthenticationFilter(jwtProvider, objectMapper), UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exception -> exception
                    .authenticationEntryPoint((request, response, e) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    })
                )
                .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/api/1/auth/*").permitAll()
                    .requestMatchers("/error").permitAll()
                    .anyRequest().authenticated()
                )
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        BindAuthenticator authenticator = new BindAuthenticator(contextSource);
		authenticator.setUserDnPatterns(new String[] {userDnPattern});

        LdapAuthenticationProvider ldapProvider = 
                new LdapAuthenticationProvider(authenticator, databaseLdapAuthoritiesPopulator);

        return new ProviderManager(ldapProvider);
    }
}
