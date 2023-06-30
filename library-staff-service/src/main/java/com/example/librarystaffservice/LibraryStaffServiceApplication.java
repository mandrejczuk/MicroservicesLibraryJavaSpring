package com.example.librarystaffservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class LibraryStaffServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(LibraryStaffServiceApplication.class, args);
    }

}
