package com.mredr.Libraray_management.service;

import com.mredr.Libraray_management.dto.BorrowedBookSummary;
import com.mredr.Libraray_management.dto.OverdueLoanSummary;
import com.mredr.Libraray_management.model.Books;
import com.mredr.Libraray_management.model.Library;
import com.mredr.Libraray_management.model.User;
import com.mredr.Libraray_management.model.enums.Availability;
import com.mredr.Libraray_management.model.enums.Borrowed;
import com.mredr.Libraray_management.repo.BookRepo;
import com.mredr.Libraray_management.repo.LibraryRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LibraryServiceTest {

    @Mock
    private LibraryRepo libraryRepo;

    @Mock
    private BookRepo bookRepo;

    @InjectMocks
    private LibraryService libraryService;

    private User user;
    private User librarian;
    private Books book;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUserId(1L);
        user.setName("Alice");

        librarian = new User();
        librarian.setUserId(2L);
        librarian.setName("Bob");

        book = new Books(10L, "Design Patterns", "GoF Book", "Software", Availability.AVAILABLE, 2);
    }

    @Test
    void testCreateBorrow_DecrementsStock_Success() {
        when(libraryRepo.save(any(Library.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Library loan = libraryService.createBorrow(user, book, librarian);

        assertNotNull(loan);
        assertEquals(1, book.getStock());
        assertEquals(Availability.AVAILABLE, book.getAvailability());
        assertEquals(Borrowed.BORROWED, loan.getBorrowed());
        verify(bookRepo).save(book);
        verify(libraryRepo).save(loan);
    }

    @Test
    void testCreateBorrow_LastStock_MarksNotAvailable() {
        book.setStock(1);
        when(libraryRepo.save(any(Library.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Library loan = libraryService.createBorrow(user, book, librarian);

        assertEquals(0, book.getStock());
        assertEquals(Availability.NOT_AVAILABLE, book.getAvailability());
    }

    @Test
    void testGetOverdueBorrows_CalculatesFinesCorrectly() {
        Library overdueLoan = new Library(LocalDate.now().minusDays(5), user, book, librarian);
        overdueLoan.setTransactionId(77L);
        overdueLoan.setBorrowed(Borrowed.BORROWED);

        when(libraryRepo.findByBorrowedAndDueDateBefore(eq(Borrowed.BORROWED), any(LocalDate.class)))
                .thenReturn(List.of(overdueLoan));

        List<OverdueLoanSummary> overdues = libraryService.getOverdueBorrows();

        assertEquals(1, overdues.size());
        OverdueLoanSummary summary = overdues.get(0);
        assertEquals(5, summary.getOverdueDays());
        assertEquals(25.0, summary.getFineAmount()); // 5 days * 5.0
    }

    @Test
    void testGetBorrowedBooksSummary() {
        Library loan = new Library(LocalDate.now().plusDays(10), user, book, librarian);
        loan.setBorrowed(Borrowed.BORROWED);

        when(libraryRepo.findByBorrowed(Borrowed.BORROWED)).thenReturn(List.of(loan));

        List<BorrowedBookSummary> summaries = libraryService.getBorrowedBooksSummary();

        assertEquals(1, summaries.size());
        assertEquals(1, summaries.get(0).getBorrowerCount());
        assertEquals(book, summaries.get(0).getBook());
    }
}
