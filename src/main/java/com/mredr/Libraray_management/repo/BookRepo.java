package com.mredr.Libraray_management.repo;

import com.mredr.Libraray_management.model.Books;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepo extends JpaRepository<Books,Long> {
}
