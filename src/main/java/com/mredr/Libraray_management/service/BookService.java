package com.mredr.Libraray_management.service;

import com.mredr.Libraray_management.model.Books;
import com.mredr.Libraray_management.model.enums.Availability;
import com.mredr.Libraray_management.model.enums.Borrowed;
import com.mredr.Libraray_management.repo.BookRepo;
import com.mredr.Libraray_management.repo.LibraryRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookService {

    @Autowired
    private BookRepo bookRepo;

    @Autowired
    private LibraryRepo libraryRepo;

    public List<Books> getBooks() {
        return bookRepo.findAll();
    }

    public Books getBookById(long id) {
        return bookRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + id));
    }

    public Books addBook(Books book) {
        if (book.getName() == null || book.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Book name cannot be empty");
        }
        if (book.getStock() < 0) {
            throw new IllegalArgumentException("Stock cannot be negative");
        }
        book.setAvailability(book.getStock() > 0 ? Availability.AVAILABLE : Availability.NOT_AVAILABLE);
        return bookRepo.save(book);
    }

    public Books updateBook(long id, Books bookDetails) {
        Books book = getBookById(id);

        if (bookDetails.getName() != null && !bookDetails.getName().trim().isEmpty()) {
            book.setName(bookDetails.getName().trim());
        }
        if (bookDetails.getDescription() != null) {
            book.setDescription(bookDetails.getDescription());
        }
        if (bookDetails.getCategory() != null) {
            book.setCategory(bookDetails.getCategory());
        }
        if (bookDetails.getStock() >= 0) {
            book.setStock(bookDetails.getStock());
            book.setAvailability(bookDetails.getStock() > 0 ? Availability.AVAILABLE : Availability.NOT_AVAILABLE);
        }

        return bookRepo.save(book);
    }

    public void deleteBook(long id) {
        Books book = getBookById(id);
        boolean hasActiveBorrows = libraryRepo.existsByBookAndBorrowed(book, Borrowed.BORROWED);
        if (hasActiveBorrows) {
            throw new IllegalStateException("Cannot delete book because there are active borrow records associated with it");
        }
        bookRepo.delete(book);
    }

    public List<Books> searchBooks(String query, String category, Availability availability) {
        List<Books> results;

        if (query != null && !query.trim().isEmpty()) {
            results = bookRepo.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(query.trim(), query.trim());
        } else if (category != null && !category.trim().isEmpty()) {
            results = bookRepo.findByCategoryIgnoreCase(category.trim());
        } else if (availability != null) {
            results = bookRepo.findByAvailability(availability);
        } else {
            results = bookRepo.findAll();
        }

        return results.stream()
                .filter(b -> category == null || category.trim().isEmpty() || b.getCategory().equalsIgnoreCase(category.trim()))
                .filter(b -> availability == null || b.getAvailability() == availability)
                .collect(Collectors.toList());
    }
}
