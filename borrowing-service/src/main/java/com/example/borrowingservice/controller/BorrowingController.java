package com.example.borrowingservice.controller;

import com.example.borrowingservice.dto.BorrowingRequest;
import com.example.borrowingservice.dto.EndBorrowingRequest;
import com.example.borrowingservice.model.Borrowing;
import com.example.borrowingservice.service.BorrowingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;



@RequiredArgsConstructor
@RestController
@RequestMapping("/api/borrowing")
public class BorrowingController {

    private final BorrowingService borrowingService;

    @GetMapping
    public Flux<Borrowing> getUserBorrowings(@RequestHeader("Authorization") String token)
    {
        return borrowingService.getUserBorrowings(token).flatMap(borrowings -> Flux.fromIterable(borrowings));
    }

    @PostMapping
    public Mono<ResponseEntity<Borrowing>> addBorrowing(@RequestHeader("Authorization") String token,@RequestBody BorrowingRequest borrowingRequest)
    {
        return borrowingService.addBorrowing(token, borrowingRequest).flatMap(borrowing -> {

          //TODO  send to book service request to change status to borrowed //

               return Mono.just(ResponseEntity.ok(borrowing));
        }).onErrorResume(e-> Mono.error(e));
    }

//    @PutMapping ("/end")
//    public Mono<ResponseEntity<Borrowing>> endBorrowing(@RequestHeader("Authorization") String token, @RequestBody EndBorrowingRequest endBorrowingRequest)
//    {
//        return borrowingService.endBorrowing(token,endBorrowingRequest).flatMap(
//                borrowing -> {
//                    return Mono.just(ResponseEntity.ok(borrowing));
//                }
//        ).onErrorResume(e->Mono.error(e));
//
//    }



}
