package com.example.borrowingservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class BorrowingServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(BorrowingServiceApplication.class, args);
    }

}
