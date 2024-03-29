package com.example.reservationservice.service;

import com.example.reservationservice.config.TokenScheduler;
import com.example.reservationservice.dto.AuditRequest;
import com.example.reservationservice.dto.BookResponse;
import com.example.reservationservice.dto.LoginRequest;
import com.example.reservationservice.dto.ReservationRequest;
import com.example.reservationservice.model.Reservation;
import com.example.reservationservice.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final WebClient.Builder webClientBuilder;


    public Mono<Reservation> createReservation(String token, String action, ReservationRequest reservationRequest)
    {
        //if token valid
        //if book status available
        //create reservation
        //then audit request
        //return Mono <Reservation>

        return isTokenValid(token).flatMap(valid->{
            if(valid)
            {
                return getBookById(reservationRequest.getBookId()).flatMap(
                        book->{

                            if(book.getStatus() == BookResponse.BookStatus.AVAILABLE) {


                                return idFromToken(token).flatMap(
                                        userId -> changeBookStatusToReserved(token,book.getId()).flatMap(bookResponse -> {

                                            AuditRequest auditRequest = AuditRequest.builder()
                                                    .action(action)
                                                    .source("reservation-service")
                                                    .userId(userId) // Pobierz ID użytkownika
                                                    .timestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                                                    .build();
                                            sendAuditEvent(auditRequest);

                                            return Mono.just(reservationRepository.save(
                                                    Reservation.builder()
                                                            .userId(Long.parseLong(userId))
                                                            .reservationDate(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                                                            .dueDate(LocalDateTime.now().plusDays(7).truncatedTo(ChronoUnit.SECONDS))
                                                            .bookId(reservationRequest.getBookId())
                                                            .build()));

                                        }).onErrorResume( e->
                                                Mono.error(e)
                                        )
                                );
                            }
                            else
                            {
                                return Mono.error(new ResponseStatusException(HttpStatus.CONFLICT));
                            }
            }
                );
            }
            else
            {
                return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED));
            }
        });


    }

    public Flux<List<Reservation>> getUserReservations(String token) {
        return isTokenValid(token)
                .flatMapMany(valid -> {
                    if (valid) {
                        return idFromToken(token)
                                .flatMapMany(userId -> {
                                    AuditRequest auditRequest = AuditRequest.builder()
                                            .action("GET")
                                            .source("reservation-service")
                                            .userId(userId)
                                            .timestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                                            .build();
                                    sendAuditEvent(auditRequest);

                                    return Flux.just(reservationRepository.getAllByUserId(Long.parseLong(userId)));
                                });
                    } else {
                        return Flux.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED));
                    }
                });
    }

    public Mono<HttpStatus> cancelReservation(String token, Long reservationId)
    {
        return isTokenValid(token).flatMap(valid->{
            if(valid)
            {
                return roleFromToken(token).flatMap(role ->{

                    if(role.equals("USER"))
                    {
                        return idFromToken(token).flatMap(userId ->{



                            if(reservationRepository.getAllByUserId(Long.parseLong(userId)).stream().anyMatch(r->r.getId().equals(reservationId)))
                            {
                                Reservation reservation = reservationRepository.getReferenceById(reservationId);

                                reservation.setDueDate(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));

                                Mono <BookResponse> bookResponseMono = changeBookStatusToAvailable(token, reservationId);


                                 reservationRepository.save(reservation);
                                 return Mono.just( HttpStatus.OK);
                            }
                            else
                            {
                                return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED));
                            }
                        });

                    }
                    else
                    {
                        Reservation reservation = reservationRepository.getReferenceById(reservationId);
                        reservation.setDueDate(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));

                        reservationRepository.save(reservation);
                         return Mono.just(HttpStatus.OK);
                    }
                });
            }
            else
            {
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
    private Mono<BookResponse> changeBookStatusToReserved(String token,Long id)
    {
        return webClientBuilder.build()
                .put()
                .uri("http://book-service/api/book/reservation", uriBuilder -> uriBuilder.queryParam("id",id).build())
                .header("Authorization", TokenScheduler.getJwtToken())
                .retrieve()
                .bodyToMono(BookResponse.class);

    }

    private Mono<BookResponse> changeBookStatusToAvailable(String token,Long id)
    {
        return webClientBuilder.build()
                .put()
                .uri("http://book-service/api/book/reservation", uriBuilder -> uriBuilder.queryParam("id",id).build())
                .header("Authorization", token)
                .retrieve()
                .bodyToMono(BookResponse.class);

    }



}
