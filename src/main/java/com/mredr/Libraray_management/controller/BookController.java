package com.mredr.Libraray_management.controller;

import com.mredr.Libraray_management.model.Books;
import com.mredr.Libraray_management.model.enums.Availability;
import com.mredr.Libraray_management.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class BookController {

    @Autowired
    private BookService bookService;


    // =========================
    // GET ALL BOOKS
    // =========================

    @GetMapping("/books")
    public List<Books> getBooks() {
        return bookService.getBooks();
    }


    // =========================
    // GET BOOK BY ID
    // =========================

    @GetMapping("/books/{id}")
    public Books getBookById(@PathVariable Long id) {
        return bookService.getBookById(id);
    }


    // =========================
    // SEARCH BOOKS
    // =========================

    @GetMapping("/books/search")
    public List<Books> searchBooks(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Availability availability
    ) {
        return bookService.searchBooks(
                query,
                category,
                availability
        );
    }


    // =========================
    // ADD BOOK
    // =========================

    @PostMapping("/librarian/books")
    public ResponseEntity<Books> addBook(
            @RequestBody Books book
    ) {

        Books savedBook = bookService.addBook(book);

        return new ResponseEntity<>(
                savedBook,
                HttpStatus.CREATED
        );
    }


    // =========================
    // UPDATE BOOK
    // =========================

    @PutMapping("/librarian/books/{id}")
    public Books updateBook(
            @PathVariable Long id,
            @RequestBody Books book
    ) {

        return bookService.updateBook(id, book);
    }


    // =========================
    // DELETE BOOK
    // =========================

    @DeleteMapping("/librarian/books/{id}")
    public ResponseEntity<String> deleteBook(
            @PathVariable Long id
    ) {

        bookService.deleteBook(id);

        return ResponseEntity.ok(
                "Book successfully deleted"
        );
    }
}