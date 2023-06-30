package com.example.libraryservice.service;

import com.example.libraryservice.repository.LibraryRepository;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class LibraryService {

    private final LibraryRepository libraryRepository;

}
