package com.example.returnservice.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "returns")
public class Return {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateReturned;

    private ReturnStatus status;
    private BigDecimal fine;
    private Long borrowingId;

    public enum ReturnStatus {
        RETURNED,
        LATE,
        DAMAGED,
        LOST,
        NOT_RETURNED
    }
}
