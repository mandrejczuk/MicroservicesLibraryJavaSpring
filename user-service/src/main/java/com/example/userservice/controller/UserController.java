package com.example.userservice.controller;


import com.example.userservice.service.UserService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/user")
@RestController
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;


    @PostMapping("/register")
    public void createUser()
    {

    }

}
