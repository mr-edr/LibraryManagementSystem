package com.mredr.Libraray_management.repo;

import com.mredr.Libraray_management.model.Books;
import com.mredr.Libraray_management.model.Library;
import com.mredr.Libraray_management.model.User;
import com.mredr.Libraray_management.model.enums.Borrowed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LibraryRepo extends JpaRepository<Library, Long> {
    List<Library> findByBorrowed(Borrowed borrowed);
    List<Library> findByUserAndBorrowed(User user, Borrowed borrowed);
    List<Library> findByUser(User user);
    List<Library> findByBorrowedAndDueDateBefore(Borrowed borrowed, LocalDate date);
    boolean existsByBookAndBorrowed(Books book, Borrowed borrowed);
}
