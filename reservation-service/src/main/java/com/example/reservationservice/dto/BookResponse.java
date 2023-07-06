package com.example.reservationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.awt.print.Book;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookResponse {

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
