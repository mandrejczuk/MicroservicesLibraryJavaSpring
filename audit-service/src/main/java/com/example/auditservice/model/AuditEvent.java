package com.example.auditservice.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Document(value = "audit_event")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuditEvent {

    @Id
    private String id;

    @Field(name = "timestamp")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    @Field(name = "action")
    private String action;

    @Field(name = "source")
    private String source;
    @Field(name = "userId")
    private String userId;

}
