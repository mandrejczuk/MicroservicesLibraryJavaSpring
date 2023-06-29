package com.example.userservice.controller;


import com.example.userservice.dto.LoginRequest;
import com.example.userservice.dto.RegisterRequest;

import com.example.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;



@RequestMapping("/api/user")
@RestController
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;


    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public String createUser(@RequestBody RegisterRequest registerRequest)
    {

        userService.registerUser(registerRequest);
        return "Registration completed successfully";
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest loginRequest)
    {


        return userService.loginUser(loginRequest);
    }

    @GetMapping("/test")
    public String test()
    {
        return "gut";
    }

    @GetMapping("/token/role")
    public String getRoleFromToken(@RequestHeader("Authorization") String token){

        return userService.getRoleFromToken(token);
    }

    @GetMapping("/token/validate")
    public boolean isTokenValid(@RequestHeader("Authorization") String token){

        return userService.validateToken(token);
    }


}
