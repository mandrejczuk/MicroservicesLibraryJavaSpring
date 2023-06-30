package com.example.libraryservice.controller;

import com.example.libraryservice.service.LibraryService;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/library")
public class LibraryController {

    private final LibraryService libraryService;


    @GetMapping
    public String libraryTest()
    {
        return "libraryTest";
    }

}
