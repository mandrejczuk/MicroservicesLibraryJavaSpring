package com.example.bookservice.service;

import com.example.bookservice.dto.AuditRequest;
import com.example.bookservice.dto.BookRequest;
import com.example.bookservice.model.Book;
import com.example.bookservice.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RequiredArgsConstructor
@Service
@Transactional
@Slf4j
public class BookService {

    private final BookRepository bookRepository;
    private final WebClient.Builder webClientBuilder;

    public Mono<AuditRequest> createAuditRequest(String token) {

        Mono<Boolean> isTokenValidMono = isTokenValid(token);
        Mono<String> userIdMono = idFromToken(token);
        Mono<String> userRoleMono = roleFromToken(token);


        return Mono.zip(isTokenValidMono, userIdMono, userRoleMono)
                .flatMap(tuple -> {
                    boolean isTokenValid = tuple.getT1();
                    String userId = tuple.getT2();
                    String userRole = tuple.getT3();

                    System.out.println(userRole);

                    if (isTokenValid) {
                        if (!userRole.equals("USER")) {
                            return Mono.just(AuditRequest.builder()
                                    .action("CREATE")
                                    .source("book-service")
                                    .userId(userId)
                                    .timestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                                    .build());
                        } else {

                            return Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN));
                        }
                    } else {
                        return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED));
                    }


                });
    }


    public void addBook(BookRequest bookRequest) {
        bookRepository.save(
                Book.builder()
                        .title(bookRequest.getTitle())
                        .author(bookRequest.getAuthor())
                        .isbn(bookRequest.getIsbn())
                        .publication(bookRequest.getPublication())
                        .status(bookRequest.getStatus())
                        .libraryId(bookRequest.getLibraryId())
                        .build()
        );
    }

    private Mono<String> roleFromToken(String token) {
        return webClientBuilder.build()
                .get()
                .uri("http://user-service/api/user/token/role")
                .header("Authorization", token)
                .retrieve()
                .bodyToMono(String.class);
    }

    private Mono<Boolean> isTokenValid(String token) {
        return webClientBuilder.build()
                .get()
                .uri("http://user-service/api/user/token/validate")
                .header("Authorization", token)
                .retrieve()
                .bodyToMono(Boolean.class);
    }

    public void sendAuditEvent(AuditRequest auditRequest) {
        webClientBuilder.build()
                .post()
                .uri("http://audit-service/api/audit")
                .body(BodyInserters.fromValue(auditRequest))
                .retrieve()
                .toEntity(String.class)
                .subscribe(responseEntity -> {
                    HttpStatus statusCode = responseEntity.getStatusCode();

                    log.info("CREATE AUDIT REQUEST STATUS {}", statusCode);

                    // Dodatkowe operacje na odpowiedzi
                    // ...
                });

    }

    public Mono<String> idFromToken(String token) {
        return webClientBuilder.build()
                .get()
                .uri("http://user-service/api/user/token/id")
                .header("Authorization", token)
                .retrieve()
                .bodyToMono(String.class);
    }


}
