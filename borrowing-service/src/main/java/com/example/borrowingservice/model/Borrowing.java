package com.example.borrowingservice.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "borrowings")
public class Borrowing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateBorrowed;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dueDate;

    private Long bookId;
    private Long userId;

}
