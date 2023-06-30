package com.example.bookservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    private String title;
    private String author;
    private String isbn;
    private LocalDate publication;
    private BookStatus status;
    private Long libraryId;

    public enum BookStatus {
        DESTROYED,
        LOST,
        BORROWED,
        AVAILABLE,
        RESERVED
    }
}
