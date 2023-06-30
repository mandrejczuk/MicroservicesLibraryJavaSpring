package com.example.librarystaffservice.repository;

import com.example.librarystaffservice.model.LibraryStaff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LibraryStaffRepository extends JpaRepository<LibraryStaff,Long> {
}
