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

    @GetMapping("/books")
    public List<Books> getBooks() {
        return bookService.getBooks();
    }

    @GetMapping("/books/{id}")
    public Books getBookById(@PathVariable long id) {
        return bookService.getBookById(id);
    }

    @GetMapping("/books/search")
    public List<Books> searchBooks(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Availability availability
    ) {
        return bookService.searchBooks(query, category, availability);
    }

    @PostMapping("/librarian/books")
    public ResponseEntity<Books> addBook(@RequestBody Books book) {
        Books savedBook = bookService.addBook(book);
        return new ResponseEntity<>(savedBook, HttpStatus.CREATED);
    }

    @PutMapping("/librarian/books/{id}")
    public Books updateBook(@PathVariable long id, @RequestBody Books book) {
        return bookService.updateBook(id, book);
    }

    @DeleteMapping("/librarian/books/{id}")
    public ResponseEntity<String> deleteBook(@PathVariable long id) {
        bookService.deleteBook(id);
        return ResponseEntity.ok("Book successfully deleted");
    }
}
