package com.mredr.Libraray_management.service;

import com.mredr.Libraray_management.model.Books;
import com.mredr.Libraray_management.model.Library;
import com.mredr.Libraray_management.model.TransactionRequest;
import com.mredr.Libraray_management.model.User;
import com.mredr.Libraray_management.model.enums.Availability;
import com.mredr.Libraray_management.model.enums.Borrowed;
import com.mredr.Libraray_management.model.enums.RequestStatus;
import com.mredr.Libraray_management.model.enums.RequestType;
import com.mredr.Libraray_management.model.enums.Role;
import com.mredr.Libraray_management.repo.BookRepo;
import com.mredr.Libraray_management.repo.LibraryRepo;
import com.mredr.Libraray_management.repo.TransactionRepo;
import com.mredr.Libraray_management.repo.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private UserRepo userRepo;

    @Mock
    private BookRepo bookRepo;

    @Mock
    private TransactionRepo transactionRepo;

    @Mock
    private LibraryRepo libraryRepo;

    @Mock
    private LibraryService libraryService;

    @InjectMocks
    private TransactionService transactionService;

    private User testUser;
    private User testLibrarian;
    private Books testBook;
    private Library testLoan;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUserId(1L);
        testUser.setName("John Doe");
        testUser.setUserName("johndoe");
        testUser.setRole(Role.USER);

        testLibrarian = new User();
        testLibrarian.setUserId(2L);
        testLibrarian.setName("Admin Lib");
        testLibrarian.setUserName("librarian");
        testLibrarian.setRole(Role.LIBRARIAN);

        testBook = new Books(10L, "Clean Architecture", "Architecture guide", "Tech", Availability.AVAILABLE, 3);

        testLoan = new Library(LocalDate.now().plusDays(14), testUser, testBook, testLibrarian);
        testLoan.setTransactionId(100L);
        testLoan.setBorrowed(Borrowed.BORROWED);
    }

    @Test
    void testBorrowRequest_Success() {
        when(bookRepo.findById(10L)).thenReturn(Optional.of(testBook));
        when(userRepo.findByUserName("johndoe")).thenReturn(testUser);
        when(transactionRepo.existsByUserAndBookAndTypeAndStatus(testUser, testBook, RequestType.BORROW, RequestStatus.PENDING))
                .thenReturn(false);
        when(transactionRepo.save(any(TransactionRequest.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TransactionRequest request = transactionService.borrowRequest(10L, "johndoe");

        assertNotNull(request);
        assertEquals(RequestType.BORROW, request.getType());
        assertEquals(RequestStatus.PENDING, request.getStatus());
        assertEquals(testBook, request.getBook());
        assertEquals(testUser, request.getUser());
    }

    @Test
    void testBorrowRequest_BookOutOfStock_ThrowsException() {
        testBook.setStock(0);
        testBook.setAvailability(Availability.NOT_AVAILABLE);

        when(bookRepo.findById(10L)).thenReturn(Optional.of(testBook));
        when(userRepo.findByUserName("johndoe")).thenReturn(testUser);

        assertThrows(IllegalStateException.class, () -> transactionService.borrowRequest(10L, "johndoe"));
    }

    @Test
    void testExtendRequest_Success() {
        when(userRepo.findByUserName("johndoe")).thenReturn(testUser);
        when(libraryRepo.findById(100L)).thenReturn(Optional.of(testLoan));
        when(transactionRepo.existsByTransactionAndTypeAndStatus(testLoan, RequestType.EXTEND, RequestStatus.PENDING))
                .thenReturn(false);
        when(transactionRepo.save(any(TransactionRequest.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TransactionRequest request = transactionService.extendRequest(100L, "johndoe");

        assertNotNull(request);
        assertEquals(RequestType.EXTEND, request.getType());
        assertEquals(RequestStatus.PENDING, request.getStatus());
        assertEquals(testLoan, request.getTransaction());
    }

    @Test
    void testReturnRequest_Success() {
        when(userRepo.findByUserName("johndoe")).thenReturn(testUser);
        when(libraryRepo.findById(100L)).thenReturn(Optional.of(testLoan));
        when(transactionRepo.existsByTransactionAndTypeAndStatus(testLoan, RequestType.RETURN, RequestStatus.PENDING))
                .thenReturn(false);
        when(transactionRepo.save(any(TransactionRequest.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TransactionRequest request = transactionService.returnRequest(100L, "johndoe");

        assertNotNull(request);
        assertEquals(RequestType.RETURN, request.getType());
        assertEquals(RequestStatus.PENDING, request.getStatus());
        assertEquals(testLoan, request.getTransaction());
    }

    @Test
    void testApproveRequest_BorrowFlow() {
        TransactionRequest pending = new TransactionRequest();
        pending.setRequestId(50L);
        pending.setUser(testUser);
        pending.setBook(testBook);
        pending.setType(RequestType.BORROW);
        pending.setStatus(RequestStatus.PENDING);

        when(transactionRepo.findById(50L)).thenReturn(Optional.of(pending));
        when(userRepo.findByUserName("librarian")).thenReturn(testLibrarian);
        when(libraryService.createBorrow(testUser, testBook, testLibrarian)).thenReturn(testLoan);
        when(transactionRepo.save(any(TransactionRequest.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TransactionRequest approved = transactionService.approveRequest(50L, "librarian");

        assertEquals(RequestStatus.ACCEPTED, approved.getStatus());
        assertNotNull(approved.getTransaction());
    }

    @Test
    void testApproveRequest_ReturnFlow_RestoresStockAndAvailability() {
        testBook.setStock(0);
        testBook.setAvailability(Availability.NOT_AVAILABLE);

        TransactionRequest returnReq = new TransactionRequest();
        returnReq.setRequestId(51L);
        returnReq.setUser(testUser);
        returnReq.setBook(testBook);
        returnReq.setTransaction(testLoan);
        returnReq.setType(RequestType.RETURN);
        returnReq.setStatus(RequestStatus.PENDING);

        when(transactionRepo.findById(51L)).thenReturn(Optional.of(returnReq));
        when(userRepo.findByUserName("librarian")).thenReturn(testLibrarian);
        when(transactionRepo.save(any(TransactionRequest.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TransactionRequest approved = transactionService.approveRequest(51L, "librarian");

        assertEquals(RequestStatus.ACCEPTED, approved.getStatus());
        assertEquals(Borrowed.RETURNED, testLoan.getBorrowed());
        assertNotNull(testLoan.getReturnDate());
        assertEquals(1, testBook.getStock());
        assertEquals(Availability.AVAILABLE, testBook.getAvailability());
        verify(bookRepo).save(testBook);
        verify(libraryRepo).save(testLoan);
    }

    @Test
    void testRejectRequest_Success() {
        TransactionRequest pending = new TransactionRequest();
        pending.setRequestId(52L);
        pending.setStatus(RequestStatus.PENDING);

        when(transactionRepo.findById(52L)).thenReturn(Optional.of(pending));
        when(transactionRepo.save(any(TransactionRequest.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TransactionRequest rejected = transactionService.rejectRequest(52L, "librarian");

        assertEquals(RequestStatus.REJECTED, rejected.getStatus());
    }
}
