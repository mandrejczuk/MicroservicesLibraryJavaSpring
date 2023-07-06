package com.example.bookservice.controller;

import com.example.bookservice.dto.BookRequest;
import com.example.bookservice.model.Book;
import com.example.bookservice.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.rmi.NoSuchObjectException;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/book")
public class BookController {

    private final BookService bookService;


    @PostMapping
    public Mono<ResponseEntity<Object>> addBook(@RequestHeader("Authorization") String token, @RequestBody BookRequest bookRequest) {
        return bookService.createAuditRequest(token,"CREATE")
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
    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable Long id)
    {
        return bookService.getBookById(id)
                .map(book -> ResponseEntity.ok(book))
                .orElse(ResponseEntity.noContent().build());
    }
    @GetMapping
    public ResponseEntity<List<Book>> getBooks()
    {
       List<Book> books = bookService.getAllBooks();

       if(books.isEmpty())
       {
           return ResponseEntity.noContent().build();
       }
       else
       {
           return ResponseEntity.ok(books);
       }
    }

    @PutMapping
    public Mono<ResponseEntity<Object>> updateBook(@RequestHeader("Authorization") String token, @RequestBody BookRequest bookRequest, @RequestParam Long id)
    {


       return   bookService.createAuditRequest(token,"UPDATE")
                 .flatMap(auditRequest -> {
                     bookService.sendAuditEvent(auditRequest);
                     try {
                         bookService.updateBook(bookRequest, id);
                     }
                     catch (NoSuchObjectException e)
                     {
                         return Mono.just(ResponseEntity.notFound().build());
                     }
                     return Mono.just(ResponseEntity.status(HttpStatus.CREATED).build());
                 })
                 .onErrorResume(e->{
                     if (e instanceof ResponseStatusException) {
                         ResponseStatusException ex = (ResponseStatusException) e;
                         return Mono.just(ResponseEntity.status(ex.getStatus()).build());
                     }
                     return Mono.error(e);
                 });
    }

    @PutMapping("/reservation")
    public Mono<ResponseEntity<Book>> changeBookStatusToReserved(@RequestHeader("Authorization") String token, @RequestParam Long id)
    {
        return bookService.changeBookStatusToReservation(token,id).flatMap(book->{

            if(book.getStatus() == Book.BookStatus.RESERVED) {
                return Mono.just(ResponseEntity.status(HttpStatus.OK).body(book));
            }
            else
            {
                return Mono.just(ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(book));
            }

        })
                .onErrorResume(e-> Mono.error(e));
    }


}
