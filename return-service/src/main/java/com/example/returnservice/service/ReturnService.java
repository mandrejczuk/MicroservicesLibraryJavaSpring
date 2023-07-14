package com.example.returnservice.service;

import com.example.returnservice.dto.AuditRequest;
import com.example.returnservice.dto.BorrowingResponse;
import com.example.returnservice.dto.ReturnRequest;
import com.example.returnservice.model.Return;
import com.example.returnservice.repository.ReturnRepository;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ReturnService {

    private final ReturnRepository returnRepository;
    private final WebClient.Builder webClientBuilder;

    public Mono<Return> createReturn(String token, ReturnRequest returnRequest) {

        //TODO CHECK IF ALREADY BORROWING ID EXISTS IN TABLE
        //TODO CHANGE BOOK STATUS AGAIN TO AVAILABLE AFTER RETURN


        return getBorrowingById(token,returnRequest.getBorrowingId()).flatMap(borrowingResponse -> {

            if(borrowingResponse.getId() != null)
            {
                return Mono.just( returnRepository.save(
                        Return.builder()
                                .dateReturned(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                                .borrowingId(borrowingResponse.getId())
                                .fine(calculateFine(borrowingResponse, returnRequest) )
                                .build()
                                )
                );
            }
            else
            {
                return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED));
            }

        });

    }

    private BigDecimal calculateFine(BorrowingResponse borrowingResponse, ReturnRequest returnRequest) {

        if(returnRequest.getFine().equals(BigDecimal.ZERO))
        {
            if(borrowingResponse.getDueDate().isAfter(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)))
            {
                return BigDecimal.ZERO;
            }
            else
            {
               return BigDecimal.valueOf(borrowingResponse.getDueDate().until(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS),ChronoUnit.DAYS) * 3 );
            }
        }
        else
        {
            return returnRequest.getFine();
        }

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
    private Mono<BorrowingResponse> getBorrowingById(String token, Long borrowingId) {
        return webClientBuilder.build()
                .get()
                .uri("http://borrowing-service/api/borrowing/{id}",borrowingId)
                .header("Authorization", token)
                .retrieve()
                .bodyToMono(BorrowingResponse.class);
    }

}
