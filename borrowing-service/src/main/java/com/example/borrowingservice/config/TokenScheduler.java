package com.example.borrowingservice.config;

import com.example.borrowingservice.dto.LoginRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class TokenScheduler {

    public static String jwtToken;

    private final WebClient.Builder webClientBuilder;

    @Autowired
    public TokenScheduler(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;


    }

    @Scheduled(fixedDelay = 3600000, initialDelay = 2000)
    private void scheduledToken ()
    {
        serviceLoggingScheduled()
                .flatMap(token -> {
                    jwtToken = token;
                    System.out.println(jwtToken);
                    return Mono.empty();
                })
                .onErrorResume(e -> Mono.error(e))
                .subscribe();

        System.out.println(jwtToken);
    }
    private Mono<String> serviceLoggingScheduled() {

        return webClientBuilder.build()
                .post()
                .uri("http://user-service/api/user/login")
                .body(BodyInserters.fromValue(LoginRequest.builder()
                        .email("book@service")
                        .password("bookservice")
                        .build()))
                .retrieve()
                .bodyToMono(String.class);
    }

    public static String getJwtToken() {
        return jwtToken;
    }


}
