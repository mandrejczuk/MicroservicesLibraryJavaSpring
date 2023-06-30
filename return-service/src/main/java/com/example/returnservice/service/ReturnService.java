package com.example.returnservice.service;

import com.example.returnservice.repository.ReturnRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ReturnService {

    private final ReturnRepository returnRepository;
}
