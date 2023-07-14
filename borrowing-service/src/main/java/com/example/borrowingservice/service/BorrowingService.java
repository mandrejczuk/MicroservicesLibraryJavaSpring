package com.example.borrowingservice.service;

import com.example.borrowingservice.dto.*;
import com.example.borrowingservice.model.Borrowing;
import com.example.borrowingservice.repository.BorrowingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;
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
import java.util.List;
import java.util.Optional;

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

        //TODO CANT BORROW IS THIS BOOK IS ALREADY BORROWED DOUBLE CHECK BOOK STATUS AND BOOK ID + BORRWOING REPO + RETURN REPO
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


    public Flux<List<Borrowing>> getUserBorrowings(String token)
    {
        return isTokenValid(token).flatMapMany(valid->{
           if(valid)
           {
               return idFromToken(token).flatMapMany(userId->{

                   Example<Borrowing> borrowingExample = Example.of(Borrowing.builder().userId(Long.parseLong(userId)).build());

                 return Flux.just(borrowingRepository.findAll(borrowingExample));

               });
           }
           else {
               return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED));
           }
        });
    }


//    public Mono <Borrowing> endBorrowing (String token, EndBorrowingRequest endBorrowingRequest)
//    {
//        return isTokenValid(token).flatMap(valid->{
//
//            if(valid)
//            {
//                return roleFromToken(token).flatMap(role->{
//
//                    if(role.equals("SERVICE"))
//                    {
//                            Borrowing borrowing = borrowingRepository.getReferenceById(endBorrowingRequest.getId());
//                            borrowing.setDueDate(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
//
//                            borrowingRepository.save(borrowing);
//
//                            return Mono.just(borrowing);
//                    }
//                    else
//                    {
//                        return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED));
//                    }
//
//                });
//            }
//            else
//            {
//                return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED));
//            }
//
//        });
//    }

    public Mono<Borrowing> getBorrowingById(String token, Long id) {

        return isTokenValid(token).flatMap(valid->{
            if(valid)
            {
                return roleFromToken(token).flatMap(role->{

                    Optional<Borrowing> borrowingOptional = borrowingRepository.findById(id);

                    if(borrowingOptional.isPresent()) {

                        if (!role.equals("USER")) {
                                return Mono.just(borrowingOptional.get());
                        } else {


                            return idFromToken(token).flatMap(userId -> {

                                if (borrowingOptional.get().getUserId().equals(Long.parseLong(userId))) {

                                    return Mono.just(borrowingOptional.get());

                                } else {

                                    return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED));
                                }

                            }).onErrorResume(e->Mono.error(e));
                        }
                    }
                    else
                    {
                        return Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND));
                    }
                });
            }
            else
            {
                return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED));
            }
        }).onErrorResume(e->Mono.error(e));

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

