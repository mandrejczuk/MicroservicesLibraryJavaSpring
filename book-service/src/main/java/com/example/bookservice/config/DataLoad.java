package com.example.bookservice.config;

import com.example.bookservice.model.Book;
import com.example.bookservice.repository.BookRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;

@Configuration
public class DataLoad {

    @Bean
    public CommandLineRunner commandLineRunner(BookRepository bookRepository){
        return args -> {
          bookRepository.save(
                  Book.builder()
                          .title("1984")
                          .author("George Orwell")
                          .isbn("978-83-287-0650-7")
                          .publication(LocalDate.of(2018,3,16))
                          .status(Book.BookStatus.AVAILABLE)
                          .libraryId(1L)
                          .build()
          );
            bookRepository.save(
                    Book.builder()
                            .title("Brave New World")
                            .author("Aldous Huxley")
                            .isbn("978-83-287-0830-3")
                            .publication(LocalDate.of(2018,3,16))
                            .status(Book.BookStatus.AVAILABLE)
                            .libraryId(1L)
                            .build()
            );
            bookRepository.save(
                    Book.builder()
                            .title("Duma Key")
                            .author("Stephen King")
                            .isbn("978-83-7648-687-1")
                            .publication(LocalDate.of(2008,9, 18))
                            .status(Book.BookStatus.AVAILABLE)
                            .libraryId(1L)
                            .build()
            );
        };
    }

}
