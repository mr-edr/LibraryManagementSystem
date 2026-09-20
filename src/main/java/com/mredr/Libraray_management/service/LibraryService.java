package com.mredr.Libraray_management.service;

import com.mredr.Libraray_management.dto.BorrowedBookSummary;
import com.mredr.Libraray_management.dto.BorrowerInfo;
import com.mredr.Libraray_management.dto.OverdueLoanSummary;
import com.mredr.Libraray_management.model.Books;
import com.mredr.Libraray_management.model.Library;
import com.mredr.Libraray_management.model.User;
import com.mredr.Libraray_management.model.enums.Availability;
import com.mredr.Libraray_management.model.enums.Borrowed;
import com.mredr.Libraray_management.repo.BookRepo;
import com.mredr.Libraray_management.repo.LibraryRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class LibraryService {

    public static final double DAILY_FINE_RATE = 5.0; // Daily fine in currency units

    @Autowired
    private LibraryRepo libraryRepo;

    @Autowired
    private BookRepo bookRepo;

    public Library createBorrow(User user, Books book, User librarian) {
        if (book.getStock() <= 0) {
            throw new IllegalStateException("Book is currently out of stock");
        }

        book.setStock(book.getStock() - 1);
        if (book.getStock() == 0) {
            book.setAvailability(Availability.NOT_AVAILABLE);
        }
        bookRepo.save(book);

        Library library = new Library(LocalDate.now().plusDays(14), user, book, librarian);
        library.setBorrowed(Borrowed.BORROWED);

        return libraryRepo.save(library);
    }

    public List<BorrowedBookSummary> getBorrowedBooksSummary() {
        List<Library> activeBorrows = libraryRepo.findByBorrowed(Borrowed.BORROWED);

        Map<Books, List<Library>> grouped = activeBorrows.stream()
                .collect(Collectors.groupingBy(Library::getBook));

        return grouped.entrySet().stream()
                .map(entry -> {
                    Books book = entry.getKey();
                    List<Library> records = entry.getValue();
                    List<BorrowerInfo> borrowers = records.stream()
                            .map(r -> new BorrowerInfo(r.getUser(), r.getDueDate()))
                            .collect(Collectors.toList());
                    return new BorrowedBookSummary(book, records.size(), borrowers);
                })
                .collect(Collectors.toList());
    }

    public List<OverdueLoanSummary> getOverdueBorrows() {
        LocalDate today = LocalDate.now();
        List<Library> overdueLoans = libraryRepo.findByBorrowedAndDueDateBefore(Borrowed.BORROWED, today);

        return overdueLoans.stream()
                .map(loan -> {
                    long overdueDays = ChronoUnit.DAYS.between(loan.getDueDate(), today);
                    double fine = overdueDays * DAILY_FINE_RATE;
                    return new OverdueLoanSummary(
                            loan.getTransactionId(),
                            loan.getBook(),
                            loan.getUser(),
                            loan.getDueDate(),
                            overdueDays,
                            fine
                    );
                })
                .collect(Collectors.toList());
    }

    public List<Library> getActiveBorrowsForUser(User user) {
        return libraryRepo.findByUserAndBorrowed(user, Borrowed.BORROWED);
    }

    public List<Library> getAllBorrowsForUser(User user) {
        return libraryRepo.findByUser(user);
    }

    public Library getBorrowById(long transactionId) {
        return libraryRepo.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Borrow record not found with id: " + transactionId));
    }
}
