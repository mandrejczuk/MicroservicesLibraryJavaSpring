package com.example.userservice.service;

import com.example.userservice.dto.AuditRequest;
import com.example.userservice.dto.LoginRequest;
import com.example.userservice.dto.RegisterRequest;
import com.example.userservice.model.User;
import com.example.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.reactive.ClientHttpRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final HttpSession httpSession;
    private final WebClient.Builder webClientBuilder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email);
    }

    public void registerUser(RegisterRequest registerRequest) {


        log.info("User {} has registered", registerRequest.getEmail());

        User user = new User();
        user.setDebt(BigDecimal.ZERO);
        user.setEmail(registerRequest.getEmail());
        user.setName(registerRequest.getName());
        user.setSurname(registerRequest.getSurname());
        user.setPassword(registerRequest.getPassword());
        user.setRole("USER");

        userRepository.save(user);

        if (user.getId() != null) {
            AuditRequest auditRequest = AuditRequest.builder()
                    .source("user-service")
                    .action("CREATE")
                    .userId(user.getId().toString())
                    .timestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                    .build();
            sendAuditEvent(auditRequest);
        }
    }

    public String loginUser(LoginRequest loginRequest)
    {
        User user = userRepository.findByEmail(loginRequest.getEmail());



        if(user != null && user.getPassword().equals(loginRequest.getPassword()) )
        {
            log.info("User {} logged in",loginRequest.getEmail());

            httpSession.setAttribute(user.getEmail(),user.getId());


            AuditRequest auditRequest = AuditRequest.builder()
                    .source("user-service")
                    .action("LOGIN")
                    .userId(user.getId().toString())
                    .timestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                    .build();
            sendAuditEvent(auditRequest);


            return jwtTokenProvider.generateToken(user.getEmail(),user.getRole());
        }
        else
        {
            return "User not found";
        }
    }

    public String getRoleFromToken(String token)
    {
        return jwtTokenProvider.getRoleFromToken(token);
    }

    public String getIdFromToken(String token)
    {
        return userRepository.findByEmail(jwtTokenProvider.getUsernameFromToken(token)).getId().toString();
    }

    public boolean validateToken(String token)
    {
        return jwtTokenProvider.validateToken(token);
    }


    private void sendAuditEvent(AuditRequest auditRequest)
    {
        webClientBuilder.build()
                .post()
                .uri("http://audit-service/api/audit")
                .body(BodyInserters.fromValue(auditRequest))
                .retrieve()
                .toEntity(String.class)
                .subscribe(responseEntity -> {
                    HttpStatus statusCode = responseEntity.getStatusCode();

                    log.info("CREATE AUDIT REQUEST STATUS {}",statusCode);

                    // Dodatkowe operacje na odpowiedzi
                    // ...
                });

    }
}
