package com.mredr.Libraray_management.repo;

import com.mredr.Libraray_management.model.Books;
import com.mredr.Libraray_management.model.enums.Availability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepo extends JpaRepository<Books, Long> {
    List<Books> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String name, String description);
    List<Books> findByCategoryIgnoreCase(String category);
    List<Books> findByAvailability(Availability availability);
}
