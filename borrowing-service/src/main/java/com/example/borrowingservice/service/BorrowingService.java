package com.example.borrowingservice.service;

import com.example.borrowingservice.repository.BorrowingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BorrowingService {

    private final BorrowingRepository borrowingRepository;

}
