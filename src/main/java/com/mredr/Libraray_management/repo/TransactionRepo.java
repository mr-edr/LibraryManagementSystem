package com.mredr.Libraray_management.repo;

import com.mredr.Libraray_management.model.Books;
import com.mredr.Libraray_management.model.Library;
import com.mredr.Libraray_management.model.TransactionRequest;
import com.mredr.Libraray_management.model.User;
import com.mredr.Libraray_management.model.enums.RequestStatus;
import com.mredr.Libraray_management.model.enums.RequestType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepo extends JpaRepository<TransactionRequest, Long> {
    TransactionRequest findByUserAndBook(User user, Books book);
    List<TransactionRequest> findByUserOrderByRequestDateDesc(User user);
    List<TransactionRequest> findByStatus(RequestStatus status);
    List<TransactionRequest> findByStatusOrderByRequestDateDesc(RequestStatus status);
    List<TransactionRequest> findAllByOrderByRequestDateDesc();
    boolean existsByUserAndBookAndTypeAndStatus(User user, Books book, RequestType type, RequestStatus status);
    boolean existsByTransactionAndTypeAndStatus(Library transaction, RequestType type, RequestStatus status);
}
