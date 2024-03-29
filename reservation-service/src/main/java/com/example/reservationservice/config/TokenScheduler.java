package com.example.reservationservice.config;

import com.example.reservationservice.dto.LoginRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


@Component
public class TokenScheduler {

    public static String jwtToken;

    private final WebClient.Builder webClientBuilder;

    public TokenScheduler(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    @Scheduled(fixedDelay = 3600000, initialDelay = 2000)
    private void scheduledToken ()
    {
        serviceLoggingScheduled().flatMap(token->{
            jwtToken = token;
            return Mono.empty();
        }).onErrorResume(e -> Mono.error(e))
                .subscribe();

    }
    private Mono<String> serviceLoggingScheduled() {

        return webClientBuilder.build()
                .post()
                .uri("http://user-service/api/user/login")
                .body(BodyInserters.fromValue(LoginRequest.builder()
                        .email("reservation@service")
                        .password("reservationservice")
                        .build()))
                .retrieve()
                .bodyToMono(String.class);
    }

    public static String getJwtToken() {
        return jwtToken;
    }


}
