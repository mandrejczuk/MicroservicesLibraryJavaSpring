package com.example.auditservice.repository;

import com.example.auditservice.model.AuditEvent;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AuditRepository extends MongoRepository<AuditEvent,String > {
}
