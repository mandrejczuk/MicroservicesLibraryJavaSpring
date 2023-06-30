package com.example.auditservice.repository;

import com.example.auditservice.model.AuditEvent;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditRepository extends MongoRepository<AuditEvent,String > {
}
