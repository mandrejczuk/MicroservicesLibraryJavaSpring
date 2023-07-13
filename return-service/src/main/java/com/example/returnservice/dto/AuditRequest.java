package com.example.returnservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuditRequest {


    private String timestamp;
    private String action;
    private String source;
    private String userId;

}
