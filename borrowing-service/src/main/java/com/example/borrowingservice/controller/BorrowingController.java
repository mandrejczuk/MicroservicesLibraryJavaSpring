package com.example.borrowingservice.controller;

import com.example.borrowingservice.service.BorrowingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/borrowing")
public class BorrowingController {

    private final BorrowingService borrowingService;

    @GetMapping
    public String borrowingTest()
    {
        return "borrowingTest";
    }



}
