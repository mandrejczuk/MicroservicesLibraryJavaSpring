package com.example.returnservice.repository;

import com.example.returnservice.model.Return;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReturnRepository extends JpaRepository<Return,Long> {
}
