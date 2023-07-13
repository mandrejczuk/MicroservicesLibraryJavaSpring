package com.example.returnservice.controller;

import com.example.returnservice.dto.ReturnRequest;
import com.example.returnservice.model.Return;
import com.example.returnservice.service.ReturnService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/return")
public class ReturnController {

    private ReturnService returnService;

    @GetMapping
    public String testReturn()
    {
        return "testReturn";
    }

    @PostMapping
    public Mono<ResponseEntity<Return>> createReturn(@RequestHeader("Authorization")String token, ReturnRequest returnRequest)
    {
        return returnService.createReturn(token,returnRequest).flatMap(r->{

            return Mono.just(ResponseEntity.status(HttpStatus.CREATED).body(r));

        }).onErrorResume(e->Mono.error(e));
    }

}
