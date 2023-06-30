package com.example.bookservice.controller;

import com.example.bookservice.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

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
}
