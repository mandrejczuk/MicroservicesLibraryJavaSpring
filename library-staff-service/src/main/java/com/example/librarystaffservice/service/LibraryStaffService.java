package com.example.librarystaffservice.service;

import com.example.librarystaffservice.repository.LibraryStaffRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class LibraryStaffService {

    private LibraryStaffRepository libraryStaffRepository;

}
