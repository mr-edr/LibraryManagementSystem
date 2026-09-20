package com.mredr.Libraray_management.service;

import com.mredr.Libraray_management.model.Books;
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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepo bookRepo;

    @Mock
    private LibraryRepo libraryRepo;

    @InjectMocks
    private BookService bookService;

    private Books sampleBook;

    @BeforeEach
    void setUp() {
        sampleBook = new Books(1L, "Spring Boot in Action", "Comprehensive guide", "Tech", Availability.AVAILABLE, 5);
    }

    @Test
    void testAddBook_Success() {
        when(bookRepo.save(any(Books.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Books saved = bookService.addBook(sampleBook);

        assertNotNull(saved);
        assertEquals(Availability.AVAILABLE, saved.getAvailability());
        verify(bookRepo).save(sampleBook);
    }

    @Test
    void testAddBook_ZeroStock_SetsNotAvailable() {
        sampleBook.setStock(0);
        when(bookRepo.save(any(Books.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Books saved = bookService.addBook(sampleBook);

        assertEquals(Availability.NOT_AVAILABLE, saved.getAvailability());
    }

    @Test
    void testDeleteBook_WithActiveBorrows_ThrowsException() {
        when(bookRepo.findById(1L)).thenReturn(Optional.of(sampleBook));
        when(libraryRepo.existsByBookAndBorrowed(sampleBook, Borrowed.BORROWED)).thenReturn(true);

        assertThrows(IllegalStateException.class, () -> bookService.deleteBook(1L));
        verify(bookRepo, never()).delete(any());
    }

    @Test
    void testDeleteBook_WithoutActiveBorrows_Success() {
        when(bookRepo.findById(1L)).thenReturn(Optional.of(sampleBook));
        when(libraryRepo.existsByBookAndBorrowed(sampleBook, Borrowed.BORROWED)).thenReturn(false);

        bookService.deleteBook(1L);

        verify(bookRepo).delete(sampleBook);
    }

    @Test
    void testSearchBooks_ByQueryAndCategory() {
        when(bookRepo.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase("Spring", "Spring"))
                .thenReturn(List.of(sampleBook));

        List<Books> result = bookService.searchBooks("Spring", "Tech", Availability.AVAILABLE);

        assertEquals(1, result.size());
        assertEquals("Spring Boot in Action", result.get(0).getName());
    }
}
