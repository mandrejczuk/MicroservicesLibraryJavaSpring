package com.example.auditservice.controller;

import com.example.auditservice.dto.AuditRequest;
import com.example.auditservice.service.AuditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@RequestMapping("/api/audit")
@RestController
@RequiredArgsConstructor
@Slf4j
public class AuditController {

    private final AuditService auditService;


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createAudit(@RequestBody AuditRequest auditRequest)
    {
        auditService.createAudit(auditRequest);

    }

}




