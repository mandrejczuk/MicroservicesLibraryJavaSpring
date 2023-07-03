package com.example.bookservice.dto;

import com.example.bookservice.model.Book;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class BookRequest {

    private String title;
    private String author;
    private String isbn;
    private LocalDate publication;
    private Book.BookStatus status;
    private Long libraryId;
}
