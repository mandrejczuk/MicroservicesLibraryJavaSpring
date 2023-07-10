package com.example.bookservice.service;

import com.example.bookservice.dto.AuditRequest;
import com.example.bookservice.dto.BookRequest;
import com.example.bookservice.model.Book;
import com.example.bookservice.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.rmi.NoSuchObjectException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional
@Slf4j
public class BookService {

    private final BookRepository bookRepository;
    private final WebClient.Builder webClientBuilder;

    public Mono<AuditRequest> createAuditRequest(String token, String action) {

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
                                    .action(action)
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


    public Optional<Book> getBookById(Long id) {

        return bookRepository.findById(id);

    }

    public List<Book> getAllBooks() {

        return bookRepository.findAll();
    }

    public void updateBook(BookRequest bookRequest, Long id) throws NoSuchObjectException {

        Optional<Book> book = bookRepository.findById(id);

        if (book.isPresent()) {


            bookRepository.save(Book.builder()
                    .title(bookRequest.getTitle())
                    .author(bookRequest.getAuthor())
                    .isbn(bookRequest.getIsbn())
                    .publication(bookRequest.getPublication())
                    .status(bookRequest.getStatus())
                    .libraryId(bookRequest.getLibraryId())
                    .id(book.get().getId())
                    .build());
        } else {
            throw new NoSuchObjectException("Book with provided id does not exist");
        }

    }

    public Mono<Book> changeBookStatusToReservation(String token, Long id) {

        return isTokenValid(token).flatMap(valid -> {
            if (valid) {
                Optional<Book> book = getBookById(id);

                if (book.isPresent()) {
                    if (book.get().getStatus() == Book.BookStatus.AVAILABLE) {
                        book.get().setStatus(Book.BookStatus.RESERVED);
                        bookRepository.save(book.get());

                        return Mono.just(book.get());

                    } else {
                        return Mono.error(new ResponseStatusException(HttpStatus.CONFLICT));
                    }
                } else {
                    return Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND));
                }
            } else {
                return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED));
            }
        });

    }

    public Mono<Book> changeBookStatusToAvailable(String token, Long id) {

        return isTokenValid(token).flatMap(valid -> {
            if (valid) {
                Optional<Book> book = getBookById(id);
                //if this user reserved,borrowed,returned this book
                if (book.isPresent()) {


                    book.get().setStatus(Book.BookStatus.AVAILABLE);
                    bookRepository.save(book.get());

                    return Mono.just(book.get());


                } else {
                    return Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND));
                }
            } else {
                return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED));
            }
        });
    }
}
