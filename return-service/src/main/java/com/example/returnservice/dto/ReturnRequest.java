package com.example.returnservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReturnRequest {

    private Long borrowingId;
    private ReturnStatus returnStatus;


    public enum ReturnStatus {
        RETURNED,
        LATE,
        DAMAGED,
        LOST,
        NOT_RETURNED
    }
}
