package com.mredr.Libraray_management.service;

import com.mredr.Libraray_management.model.Books;
import com.mredr.Libraray_management.repo.BookRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    @Autowired
    BookRepo bookRepo;

    public List getBooks() {
        return bookRepo.findAll();
    }

    public Books getBookById(long id) {
        return bookRepo.findById(id).orElse(null);
    }
}
