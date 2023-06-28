package com.example.auditservice.controller;

import com.example.auditservice.dto.AuditRequest;
import com.example.auditservice.service.AuditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.HttpMethod;

@RequestMapping("/api/audit")
@RestController
@RequiredArgsConstructor
@Slf4j
public class AuditController {

    private final AuditService auditService;


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    private void createAudit(AuditRequest auditRequest)
    {
        auditService.createAudit(auditRequest);
    }

}
