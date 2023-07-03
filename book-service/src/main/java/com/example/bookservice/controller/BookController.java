package com.example.bookservice.controller;

import com.example.bookservice.dto.BookRequest;
import com.example.bookservice.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/book")
public class BookController {

    private final BookService bookService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public String bookTest()
    {
        return "bookTest";
    }

    @PostMapping
    public Mono<ResponseEntity<Object>> addBook(@RequestHeader("Authorization") String token, @RequestBody BookRequest bookRequest) {
        return bookService.createAuditRequest(token)
                .flatMap(auditRequest -> {
                    bookService.sendAuditEvent(auditRequest);
                    bookService.addBook(bookRequest);
                    return Mono.just(ResponseEntity.status(HttpStatus.CREATED).build());
                })
                .onErrorResume(e -> {
                    if (e instanceof ResponseStatusException) {
                        ResponseStatusException ex = (ResponseStatusException) e;
                        return Mono.just(ResponseEntity.status(ex.getStatus()).build());
                    }
                    return Mono.error(e);
                });
    }


}
