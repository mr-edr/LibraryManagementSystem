package com.mredr.Libraray_management.repo;

import com.mredr.Libraray_management.model.Library;
import com.mredr.Libraray_management.model.enums.Borrowed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LibraryRepo extends JpaRepository<Library,Long> {
    List<Library> findByBorrowed(Borrowed borrowed);
}
