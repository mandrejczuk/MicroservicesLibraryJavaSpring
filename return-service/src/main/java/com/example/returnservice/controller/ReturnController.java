package com.example.returnservice.controller;

import com.example.returnservice.service.ReturnService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
