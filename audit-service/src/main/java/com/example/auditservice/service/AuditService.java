package com.example.auditservice.service;

import com.example.auditservice.dto.AuditRequest;
import com.example.auditservice.model.AuditEvent;
import com.example.auditservice.repository.AuditRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AuditService {

    private final AuditRepository auditRepository;

    public void createAudit(AuditRequest auditRequest) {

        log.info("CREATE AUDIT CALLED");

        auditRepository.save(
                AuditEvent.builder()
                        .action(auditRequest.getAction())
                        .source(auditRequest.getSource())
                        .timestamp(LocalDateTime.parse(auditRequest.getTimestamp()))
                        .userId(auditRequest.getUserId())
                        .build()
        );

    }
}
