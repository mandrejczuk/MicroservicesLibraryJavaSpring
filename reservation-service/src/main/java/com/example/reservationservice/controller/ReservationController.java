package com.example.reservationservice.controller;

import com.example.reservationservice.dto.ReservationRequest;
import com.example.reservationservice.model.Reservation;
import com.example.reservationservice.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/reservation")
public class ReservationController {

    private final ReservationService reservationService;

//    @GetMapping
//    public String reservationTest()
//    {
//        return "reservationTest";
//    }

    @PostMapping
    public Mono<ResponseEntity<Reservation>> addReservation(@RequestHeader("Authorization") String token,@RequestBody ReservationRequest reservationRequest)
    {
        return reservationService.createReservation(token,"CREATE",reservationRequest).flatMap(
                reservation -> {
                        if(reservation.getId() != null)
                        {
                            return Mono.just(ResponseEntity.status(HttpStatus.CREATED).body(reservation));
                        }
                        else
                        {
                            return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Reservation.builder().build()));
                        }
                }
        ).onErrorResume(e-> Mono.error(e));
    }

    @GetMapping
    public Flux<Reservation> getUserReservations(@RequestHeader("Authorization") String token) {
        return reservationService.getUserReservations(token)
                .flatMap(reservations -> {
                    if (reservations.isEmpty()) {
                        return Flux.empty();
                    } else {
                        return Flux.fromIterable(reservations);
                    }
                });
    }
}
