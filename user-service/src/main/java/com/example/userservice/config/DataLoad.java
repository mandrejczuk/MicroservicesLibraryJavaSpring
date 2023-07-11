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
                            .email("user2@user2")
                            .debt(BigDecimal.ZERO)
                            .name("Jane")
                            .surname("Led")
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
            userRepository.save(
                    User.builder()
                            .email("user@service")
                            .debt(BigDecimal.ZERO)
                            .name("User")
                            .surname("Service")
                            .role("SERVICE")
                            .password("userservice")
                            .build()
            );
            userRepository.save(
                    User.builder()
                            .email("book@service")
                            .debt(BigDecimal.ZERO)
                            .name("Book")
                            .surname("Service")
                            .role("SERVICE")
                            .password("bookservice")
                            .build()
            );
            userRepository.save(
                    User.builder()
                            .email("reservation@service")
                            .debt(BigDecimal.ZERO)
                            .name("Reservation")
                            .surname("Service")
                            .role("SERVICE")
                            .password("reservationservice")
                            .build()
            );
            userRepository.save(
                    User.builder()
                            .email("borrowing@service")
                            .debt(BigDecimal.ZERO)
                            .name("Borrowing")
                            .surname("Service")
                            .role("SERVICE")
                            .password("borrowingservice")
                            .build()
            );
            userRepository.save(
                    User.builder()
                            .email("return@service")
                            .debt(BigDecimal.ZERO)
                            .name("Return")
                            .surname("Service")
                            .role("SERVICE")
                            .password("returnservice")
                            .build()
            );
            userRepository.save(
                    User.builder()
                            .email("library@service")
                            .debt(BigDecimal.ZERO)
                            .name("Library")
                            .surname("Service")
                            .role("SERVICE")
                            .password("libraryservice")
                            .build()
            );
            userRepository.save(
                    User.builder()
                            .email("librarystaff@service")
                            .debt(BigDecimal.ZERO)
                            .name("Library-Staff")
                            .surname("Service")
                            .role("SERVICE")
                            .password("librarystaffservice")
                            .build()
            );
            userRepository.save(
                    User.builder()
                            .email("audit@service")
                            .debt(BigDecimal.ZERO)
                            .name("Audit")
                            .surname("Service")
                            .role("SERVICE")
                            .password("auditservice")
                            .build()
            );
        };
    }

}
