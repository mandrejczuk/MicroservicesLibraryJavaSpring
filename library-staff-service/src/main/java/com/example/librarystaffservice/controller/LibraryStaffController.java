package com.example.librarystaffservice.controller;

import com.example.librarystaffservice.service.LibraryStaffService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/staff")
public class LibraryStaffController {

    private final LibraryStaffService libraryStaffService;

    @GetMapping
    public String libraryStaffTest()
    {
        return "libraryStaffTest";
    }

}
