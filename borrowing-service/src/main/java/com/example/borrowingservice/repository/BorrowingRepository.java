package com.example.borrowingservice.repository;

import com.example.borrowingservice.model.Borrowing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BorrowingRepository extends JpaRepository<Borrowing,Long> {
}
