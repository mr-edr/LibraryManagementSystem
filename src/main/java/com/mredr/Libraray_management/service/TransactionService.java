package com.mredr.Libraray_management.service;

import com.mredr.Libraray_management.model.*;
import com.mredr.Libraray_management.model.enums.Availability;
import com.mredr.Libraray_management.model.enums.Borrowed;
import com.mredr.Libraray_management.model.enums.RequestStatus;
import com.mredr.Libraray_management.model.enums.RequestType;
import com.mredr.Libraray_management.repo.BookRepo;
import com.mredr.Libraray_management.repo.TransactionRepo;
import com.mredr.Libraray_management.repo.UserRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;


@Service
public class TransactionService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private BookRepo bookRepo;

    @Autowired
    private TransactionRepo transactionRepo;

    @Autowired
    private LibraryService libraryService;

    public TransactionRequest borrowRequest(long bookId, String username) {
        Books book = bookRepo.findById(bookId).orElseThrow(() -> new RuntimeException("Book not found"));
        User user = userRepo.findByUserName(username);

        if(user==null)
            throw (new RuntimeException("User not found"));

        TransactionRequest request = new TransactionRequest();
        request.setUser(user);
        request.setBook(book);
        request.setType(RequestType.BORROW);
        request.setStatus(RequestStatus.PENDING);
        request.setRequestDate(LocalDate.now());

        return transactionRepo.save(request);

    }

    public List<TransactionRequest> getRequests() {
        return transactionRepo.findAll();
    }

    @Transactional
    public TransactionRequest approveRequest(long requestId, String librarianName) {
        TransactionRequest request = transactionRepo.findById(requestId).orElseThrow(() -> new RuntimeException("Request not found"));
        User librarian = userRepo.findByUserName(librarianName);

        if(request.getType()==RequestType.BORROW && request.getBook().getAvailability()== Availability.AVAILABLE){
            request.setStatus(RequestStatus.ACCEPTED);

            Library libray = libraryService.createBorrow(request.getUser(),request.getBook(),librarian);
            request.setTransaction(libray);
        }
        else if(request.getType()==RequestType.EXTEND){
            request.setStatus(RequestStatus.ACCEPTED);
            request.getTransaction().setDueDate(request.getTransaction().getDueDate().plusDays(14));
        } else if (request.getType()==RequestType.RETURN) {
            request.setStatus(RequestStatus.ACCEPTED);
            request.getTransaction().setBorrowed(Borrowed.RETURNED);
            request.getBook().setStock(request.getBook().getStock()+1);
        }

        return transactionRepo.save(request);
    }

    public TransactionRequest returnRequest(long transactionId) {
        TransactionRequest borrowrequest = transactionRepo.findById(transactionId).orElseThrow(()->new RuntimeException("Transaction not found"));

        TransactionRequest request = new TransactionRequest(
                borrowrequest.getBook(),
                RequestType.RETURN,
                borrowrequest.getUser(),
                RequestStatus.PENDING,
                LocalDate.now()
        );


        request.setTransaction(borrowrequest.getTransaction());
        return transactionRepo.save(request);

    }
}
