package com.mredr.Libraray_management.service;

import com.mredr.Libraray_management.model.*;
import com.mredr.Libraray_management.model.enums.Availability;
import com.mredr.Libraray_management.model.enums.Borrowed;
import com.mredr.Libraray_management.model.enums.RequestStatus;
import com.mredr.Libraray_management.model.enums.RequestType;
import com.mredr.Libraray_management.repo.BookRepo;
import com.mredr.Libraray_management.repo.LibraryRepo;
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
    private LibraryRepo libraryRepo;

    @Autowired
    private LibraryService libraryService;

    public TransactionRequest borrowRequest(long bookId, String username) {
        Books book = bookRepo.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + bookId));
        User user = userRepo.findByUserName(username);
        if (user == null) {
            throw new RuntimeException("User not found: " + username);
        }

        if (book.getStock() <= 0 || book.getAvailability() == Availability.NOT_AVAILABLE) {
            throw new IllegalStateException("Book is currently unavailable for borrowing");
        }

        boolean alreadyPending = transactionRepo.existsByUserAndBookAndTypeAndStatus(
                user, book, RequestType.BORROW, RequestStatus.PENDING
        );
        if (alreadyPending) {
            throw new IllegalStateException("You already have a pending borrow request for this book");
        }

        TransactionRequest request = new TransactionRequest();
        request.setUser(user);
        request.setBook(book);
        request.setType(RequestType.BORROW);
        request.setStatus(RequestStatus.PENDING);
        request.setRequestDate(LocalDate.now());

        return transactionRepo.save(request);
    }

    public TransactionRequest extendRequest(long transactionId, String username) {
        User user = userRepo.findByUserName(username);
        if (user == null) {
            throw new RuntimeException("User not found: " + username);
        }

        Library loan = libraryRepo.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Active borrow record not found with id: " + transactionId));

        if (loan.getUser().getUserId() != user.getUserId()) {
            throw new IllegalStateException("You can only request extension for your own borrowed books");
        }

        if (loan.getBorrowed() != Borrowed.BORROWED) {
            throw new IllegalStateException("Cannot extend a book that is not currently borrowed");
        }

        boolean alreadyPending = transactionRepo.existsByTransactionAndTypeAndStatus(
                loan, RequestType.EXTEND, RequestStatus.PENDING
        );
        if (alreadyPending) {
            throw new IllegalStateException("An extension request is already pending for this borrow");
        }

        TransactionRequest request = new TransactionRequest();
        request.setUser(user);
        request.setBook(loan.getBook());
        request.setTransaction(loan);
        request.setType(RequestType.EXTEND);
        request.setStatus(RequestStatus.PENDING);
        request.setRequestDate(LocalDate.now());

        return transactionRepo.save(request);
    }

    public TransactionRequest returnRequest(long transactionId, String username) {
        User user = userRepo.findByUserName(username);
        if (user == null) {
            throw new RuntimeException("User not found: " + username);
        }

        Library loan = libraryRepo.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Active borrow record not found with id: " + transactionId));

        if (loan.getUser().getUserId() != user.getUserId()) {
            throw new IllegalStateException("You can only return books borrowed under your own account");
        }

        if (loan.getBorrowed() != Borrowed.BORROWED) {
            throw new IllegalStateException("This book is not currently marked as borrowed");
        }

        boolean alreadyPending = transactionRepo.existsByTransactionAndTypeAndStatus(
                loan, RequestType.RETURN, RequestStatus.PENDING
        );
        if (alreadyPending) {
            throw new IllegalStateException("A return request is already pending for this borrow");
        }

        TransactionRequest request = new TransactionRequest();
        request.setUser(user);
        request.setBook(loan.getBook());
        request.setTransaction(loan);
        request.setType(RequestType.RETURN);
        request.setStatus(RequestStatus.PENDING);
        request.setRequestDate(LocalDate.now());

        return transactionRepo.save(request);
    }

    public List<TransactionRequest> getUserRequests(String username) {
        User user = userRepo.findByUserName(username);
        if (user == null) {
            throw new RuntimeException("User not found: " + username);
        }
        return transactionRepo.findByUserOrderByRequestDateDesc(user);
    }

    public List<TransactionRequest> getRequests(RequestStatus status) {
        if (status != null) {
            return transactionRepo.findByStatusOrderByRequestDateDesc(status);
        }
        return transactionRepo.findAllByOrderByRequestDateDesc();
    }

    @Transactional
    public TransactionRequest approveRequest(long requestId, String librarianName) {
        TransactionRequest request = transactionRepo.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found with id: " + requestId));

        if (request.getStatus() != RequestStatus.PENDING) {
            throw new IllegalStateException("Request has already been processed with status: " + request.getStatus());
        }

        User librarian = userRepo.findByUserName(librarianName);
        if (librarian == null) {
            throw new RuntimeException("Librarian not found: " + librarianName);
        }

        if (request.getType() == RequestType.BORROW) {
            Books book = request.getBook();
            if (book.getStock() <= 0 || book.getAvailability() == Availability.NOT_AVAILABLE) {
                throw new IllegalStateException("Cannot approve borrow: Book is out of stock");
            }
            request.setStatus(RequestStatus.ACCEPTED);
            Library library = libraryService.createBorrow(request.getUser(), book, librarian);
            request.setTransaction(library);
        } else if (request.getType() == RequestType.EXTEND) {
            Library loan = request.getTransaction();
            if (loan == null) {
                throw new IllegalStateException("No associated loan record found for extension request");
            }
            request.setStatus(RequestStatus.ACCEPTED);
            loan.setDueDate(loan.getDueDate().plusDays(14));
            libraryRepo.save(loan);
        } else if (request.getType() == RequestType.RETURN) {
            Library loan = request.getTransaction();
            if (loan == null) {
                throw new IllegalStateException("No associated loan record found for return request");
            }
            request.setStatus(RequestStatus.ACCEPTED);
            loan.setBorrowed(Borrowed.RETURNED);
            loan.setReturnDate(LocalDate.now());
            libraryRepo.save(loan);

            Books book = request.getBook();
            book.setStock(book.getStock() + 1);
            book.setAvailability(Availability.AVAILABLE);
            bookRepo.save(book);
        }

        return transactionRepo.save(request);
    }

    @Transactional
    public TransactionRequest rejectRequest(long requestId, String librarianName) {
        TransactionRequest request = transactionRepo.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found with id: " + requestId));

        if (request.getStatus() != RequestStatus.PENDING) {
            throw new IllegalStateException("Request has already been processed with status: " + request.getStatus());
        }

        request.setStatus(RequestStatus.REJECTED);
        return transactionRepo.save(request);
    }
}
