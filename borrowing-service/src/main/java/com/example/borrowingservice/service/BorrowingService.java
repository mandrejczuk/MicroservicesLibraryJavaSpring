package com.example.borrowingservice.service;

import com.example.borrowingservice.dto.AuditRequest;
import com.example.borrowingservice.dto.BookResponse;
import com.example.borrowingservice.dto.BorrowingRequest;
import com.example.borrowingservice.dto.ReservationResponse;
import com.example.borrowingservice.model.Borrowing;
import com.example.borrowingservice.repository.BorrowingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class BorrowingService {

    private final BorrowingRepository borrowingRepository;
    private final WebClient.Builder webClientBuilder;


    public Mono<Borrowing> addBorrowing(String token, BorrowingRequest borrowingRequest) {
        //isTokenValid
        //isBookFree-status
        //createBorrowing

        return isTokenValid(token).flatMap(valid -> {
            if (valid) {
                return idFromToken(token).flatMap(userId-> {

                    return getBookById(borrowingRequest.getBookId()).flatMap(bookResponse -> {
                        System.out.println(bookResponse.getAuthor());
                        if (bookResponse.getStatus() == BookResponse.BookStatus.AVAILABLE) {



                            return Mono.just(borrowingRepository.save(Borrowing.builder()
                                    .bookId(bookResponse.getId())
                                    .dateBorrowed(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                                    .dueDate(LocalDateTime.now().plusDays(7).truncatedTo(ChronoUnit.SECONDS))
                                    .userId(Long.parseLong(userId))
                                    .build()));
                        } else {

                            if(bookResponse.getStatus() == BookResponse.BookStatus.RESERVED)
                            {
                                //check if this user reserved
                                return getUserReservations(token).collectList().flatMap(reservationList->{

                                    if(reservationList.stream().anyMatch(reservationResponse -> reservationResponse.getUserId().equals(Long.parseLong(userId))))
                                    {
                                        return Mono.just(borrowingRepository.save(Borrowing.builder()
                                                .bookId(bookResponse.getId())
                                                .dateBorrowed(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                                                .dueDate(LocalDateTime.now().plusDays(7).truncatedTo(ChronoUnit.SECONDS))
                                                .userId(Long.parseLong(userId))
                                                .build()));
                                    }
                                    else
                                    {
                                        return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED));
                                    }

                                }) ;

                            }
                            else
                            {
                                return Mono.error(new ResponseStatusException(HttpStatus.CONFLICT));
                            }

                        }

                    });
                });
                }

            else {
                return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED));
            }

        });

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

    private Mono<String> idFromToken(String token) {
        return webClientBuilder.build()
                .get()
                .uri("http://user-service/api/user/token/id")
                .header("Authorization", token)
                .retrieve()
                .bodyToMono(String.class);
    }
    private Mono<BookResponse> getBookById(Long id)
    {
        return webClientBuilder.build()
                .get()
                .uri("http://book-service/api/book/{id}",id)
                .retrieve()
                .bodyToMono(BookResponse.class);

    }
    private Flux<ReservationResponse> getUserReservations(String token)
    {
        return webClientBuilder.build()
                .get()
                .uri("http://reservation-service/api/reservation")
                .header("Authorization", token)
                .retrieve()
                .bodyToFlux(ReservationResponse.class);

    }
}

