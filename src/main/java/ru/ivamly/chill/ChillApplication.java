package ru.ivamly.chill;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@EnableMethodSecurity
@SpringBootApplication
public class ChillApplication {

    static void main() {
        SpringApplication.run(ChillApplication.class);
    }

}
