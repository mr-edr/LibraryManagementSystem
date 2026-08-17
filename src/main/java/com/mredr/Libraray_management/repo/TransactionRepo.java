package com.mredr.Libraray_management.repo;

import com.mredr.Libraray_management.model.Books;
import com.mredr.Libraray_management.model.Library;
import com.mredr.Libraray_management.model.TransactionRequest;
import com.mredr.Libraray_management.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TransactionRepo extends JpaRepository<TransactionRequest , Long> {
    TransactionRequest findByUserAndBook(User user, Books book);
}
