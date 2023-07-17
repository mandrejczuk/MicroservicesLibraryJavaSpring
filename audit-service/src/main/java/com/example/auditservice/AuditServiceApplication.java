package com.example.auditservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class AuditServiceApplication {

    //TODO make image of this service try to do dockercompse yaml file

    public static void main(String[] args) {
        SpringApplication.run(AuditServiceApplication.class, args);
    }

}
