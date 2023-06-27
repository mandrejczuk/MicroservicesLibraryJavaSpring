package com.example.userservice.service;

import com.example.userservice.dto.LoginRequest;
import com.example.userservice.dto.RegisterRequest;
import com.example.userservice.model.User;
import com.example.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;


@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final HttpSession httpSession;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email);
    }

    public void registerUser(RegisterRequest registerRequest) {

        userRepository.save(
                User.builder()
                        .email(registerRequest.getEmail())
                        .name(registerRequest.getName())
                        .surname(registerRequest.getSurname())
                        .password(registerRequest.getPassword())
                        .role("USER")
                        .debt(BigDecimal.ZERO)
                        .build()
        );

    }

    public String loginUser(LoginRequest loginRequest)
    {
        User user = userRepository.findByEmail(loginRequest.getEmail());


        System.out.println(user.getPassword() +" :::::::::: "+loginRequest.getPassword());
        System.out.println(user.getPassword().equals(loginRequest.getPassword()) );
        if(user != null && user.getPassword().equals(loginRequest.getPassword()) )
        {
            httpSession.setAttribute(user.getEmail(),user.getId());

            return jwtTokenProvider.generateToken(user.getEmail(),user.getRole());
        }
        else
        {
            return "User not found";
        }
    }
}
