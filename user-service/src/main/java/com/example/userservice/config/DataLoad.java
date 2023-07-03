package com.example.userservice.config;

import com.example.userservice.model.User;
import com.example.userservice.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration
public class DataLoad {

    @Bean
    public CommandLineRunner commandLineRunner(UserRepository userRepository){

        return args -> {
            userRepository.save(
                    User.builder()
                            .email("user@user")
                            .debt(BigDecimal.ZERO)
                            .name("John")
                            .surname("Doe")
                            .role("USER")
                            .password("user")
                            .build()
            );
            userRepository.save(
                    User.builder()
                            .email("admin@admin")
                            .debt(BigDecimal.ZERO)
                            .name("Gustavo")
                            .surname("Fring")
                            .role("ADMIN")
                            .password("admin")
                            .build()
            );
            userRepository.save(
                    User.builder()
                            .email("staff@staff")
                            .debt(BigDecimal.ZERO)
                            .name("Mike")
                            .surname("Ehrmantraut")
                            .role("STAFF")
                            .password("staff")
                            .build()
            );
        };
    }

}