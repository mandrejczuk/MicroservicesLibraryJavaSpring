package com.example.returnservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class ReturnServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReturnServiceApplication.class, args);
    }

}
